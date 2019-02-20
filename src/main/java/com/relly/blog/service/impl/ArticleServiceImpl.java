package com.relly.blog.service.impl;

import com.relly.blog.common.config.MessageEventHandler;
import com.relly.blog.common.model.PageResult;
import com.relly.blog.dto.*;
import com.relly.blog.entity.*;
import com.relly.blog.mapper.*;
import com.relly.blog.service.ArticleService;
import com.relly.blog.utils.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private ArticleMessageMapper articleMessageMapper;
    @Resource
    private NoticeMapper noticeMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserDetailMapper userDetailMapper;



    @Override
    public List<ArticleDTO> getArticleListByUser(String userId) {
        List<ArticleDTO> list = articleMapper.getArticleListByUser(userId);
        return list;
    }

    @Override
    public PageResult getMyArticleListMore(String userId, int pageCurrent) {
        int rowCount = articleMapper.gettMyArticleListCount(userId);
        PageResult<ArticleDTO> pageResult = new PageResult<>(pageCurrent, 1, rowCount);

        List<ArticleDTO> list = articleMapper.getMyArticleListMore(userId,pageResult);
        for(ArticleDTO articleDTO : list){
            loopArticleType: for(ArticleTypeEnum articleTypeEnum : ArticleTypeEnum.values()){
                if(articleTypeEnum.getKey().toString().equals(articleDTO.getType())){
                    articleDTO.setArticleTypeStr(articleTypeEnum.getValue());
                    break loopArticleType;
                }
            }
        }
        pageResult.setPageData(list);
        return pageResult;
    }

    @Override
    public void save(UserEntity currentUser, ArticleDTO articleDTO) {
        currentUser = userMapper.selectByPrimaryKey(currentUser.getId());
        ArticleEntity articleEntity = ArticleEntity.builder()
                .content(articleDTO.getContent())
                .title(articleDTO.getTitle())
                .cover(articleDTO.getCover())
                .createTime(new Date())
                .type(articleDTO.getType())
                .isPublic(articleDTO.getIsPublic()?0:1)
                .description(articleDTO.getDescription())
                .href("总之")
                .id(IdUtil.randomId())
                .ownerName(currentUser.getName())
                .createUser(currentUser.getId())
                .build();
        articleMapper.insertSelective(articleEntity);

    }

    @Override
    public ArticleDTO getArticleDetail(String articleId) {
        ArticleDTO articleDTO = articleMapper.getArticleDetail(articleId);
        return articleDTO;
    }

    @Override
    public PageResult<ArticleMessageDTO> getArticleMessageDetail(String articleId, Integer pageSize, Integer pageCurrent) {
        int count = articleMapper.getArticleMessageCount(articleId);
        PageResult<ArticleMessageDTO> pageResult = new PageResult<>(pageCurrent, pageSize, count);
        List<ArticleMessageDTO> articleMessageDTOList = articleMapper.getArticleMessage(articleId,pageResult);
        List<ArticleMessageDTO> articleMessageDTOChildrenList = new ArrayList<>();
        for (ArticleMessageDTO am: articleMessageDTOList) {
            articleMessageDTOChildrenList = articleMapper.getArticleMessageChildren(am.getId(),am.getArticleId());
            am.setChildren(articleMessageDTOChildrenList);
        }
        pageResult.setPageData(articleMessageDTOList);
        return pageResult;
    }

    @Override
    @Transactional
    public void addMessageForArticle(String userId,AddArticleMessageDTO articleMessageDTO) {
        ArticleMessageEntity articleMessageEntity = new ArticleMessageEntity();
        int type = 0;//0评论别人的评论 , 1直接评论文章
        ArticleEntity articleEntity = articleMapper.selectByPrimaryKey(articleMessageDTO.getArticleId());
        if (articleMessageDTO.getAite()==null){
            //评论文章
            type = 1;
            articleMessageDTO.setAite(articleEntity.getCreateUser());
        }
        BeanUtils.copyProperties(articleMessageDTO, articleMessageEntity);
        Date commentDate = new Date();
        articleMessageEntity.setCreateTime(commentDate);
        articleMessageEntity.setCreateUser(userId);
        articleMessageEntity.setId(IdUtil.randomId());
        articleMessageEntity.setIsDelete(false);
        articleMessageMapper.insert(articleMessageEntity);

        //消息通知给被评论的人
        NoticeEntity noticeEntity = new NoticeEntity();
        noticeEntity.setDescription(articleMessageDTO.getContent());

        UserEntity userEntity = userMapper.selectByPrimaryKey(userId);
        if(type==1){
            noticeEntity.setTitle(userEntity.getName()+" 评论了你的文章《"+articleEntity.getTitle()+"》");
        }
        if(type==0){
            noticeEntity.setTitle(userEntity.getName()+" 回复了你");
        }

        //保存通知
        noticeEntity.setAvatar("https://gw.alipayobjects.com/zos/rmsportal/ThXAXghbEsBCCSDihZxY.png");
        noticeEntity.setDatetime(commentDate);
        noticeEntity.setSendId(userId);
        noticeEntity.setUserId(articleMessageDTO.getAite());
        noticeEntity.setId(IdUtil.randomId());
        noticeEntity.setIsRead(0);
        noticeEntity.setType(NoticeTypeEnum.MESSAGE.getState());
        noticeMapper.insertSelective(noticeEntity);

        //推送消息
        Map<String,Object> noticeContentMap = new HashMap<>();
        noticeContentMap.put("noticeTitle",noticeEntity.getTitle());
        noticeContentMap.put("noticeContent",articleMessageDTO.getContent());
        List<String> list = new ArrayList<>();
        list.add(userId);
        MessageEventHandler.sendBuyLogEvent(noticeContentMap,list);
    }
}
