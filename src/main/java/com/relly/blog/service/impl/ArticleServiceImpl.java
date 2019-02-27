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
import org.springframework.beans.factory.annotation.Value;
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

    @Resource
    private ArticleStarMapper articleStarMapper;

    @Value("${spring.profiles.active}")
    private String serverEnv;




    @Override
    public List<ArticleDTO> getArticleListByUser(String userId) {
        List<ArticleDTO> list = articleMapper.getArticleListByUser(userId);
        list = handleArticleDTOList(list);
        return list;
    }

    public List<ArticleDTO> handleArticleDTOList(List<ArticleDTO> list){
        int starNum;
        int isStarNum;
        int messageNum;
        for(ArticleDTO articleDTO : list){
            starNum = articleStarMapper.getStarNumByArticleId(articleDTO.getArticleId());
            articleDTO.setStarNum(starNum);

            isStarNum = articleStarMapper.getStarNumByArticleIdAndUserId(articleDTO.getArticleId(),articleDTO.getCreateUser());
            articleDTO.setIsStar(isStarNum>0);

            messageNum = articleMessageMapper.getMessageNumByArticleId(articleDTO.getArticleId());
            articleDTO.setMessageNum(messageNum);
            loopArticleType: for(ArticleTypeEnum articleTypeEnum : ArticleTypeEnum.values()){
                if(articleTypeEnum.getKey().toString().equals(articleDTO.getType())){
                    articleDTO.setArticleTypeStr(articleTypeEnum.getValue());
                    break loopArticleType;
                }
            }
        }
        return list;
    }


    @Override
    public PageResult getMyArticleListMore(String userId, int pageCurrent) {
        int rowCount = articleMapper.gettMyArticleListCount(userId);
        PageResult<ArticleDTO> pageResult = new PageResult<>(pageCurrent, 1, rowCount);

        List<ArticleDTO> list = articleMapper.getMyArticleListMore(userId,pageResult);
        list = handleArticleDTOList(list);
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
        List<ArticleMessageDTO> articleMessageDTOList = new ArrayList<>();
        if (serverEnv.equals("test")){
            articleMessageDTOList = articleMapper.getArticleMessage1(articleId,pageResult);
        }else {
            articleMessageDTOList = articleMapper.getArticleMessage(articleId,pageResult);
        }
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

    @Override
    public void likeArticle(String articleId) {
        ArticleEntity articleEntity = articleMapper.selectByPrimaryKey(articleId);
        Integer likeNum = articleEntity.getLikeNum();
        if(articleEntity.getLikeNum()==null){
            likeNum = 0;
        }
        articleEntity.setLikeNum(likeNum+1);
        articleMapper.updateByPrimaryKeySelective(articleEntity);
    }

    @Override
    public String starArticle(String userId,String articleId) {
        Integer isStarNum = articleStarMapper.getStarNumByArticleIdAndUserId(articleId,userId);
        if(isStarNum>0){
            articleStarMapper.delRecord(articleId);
            return ArticleStarEnum.CANCLE.getValue();
        }else {
            ArticleStarEntity articleStarEntity = new ArticleStarEntity();
            articleStarEntity.setArticleId(articleId);
            articleStarEntity.setUserId(userId);
            articleStarMapper.insert(articleStarEntity);
            return ArticleStarEnum.STAR.getValue();
        }


    }

    @Override
    public PageResult<ArticleDTO> getAllArticleList(ArticleFilterDTO articleFilterDTO,int pageCurrent) {
        int rowCount = articleMapper.getAllArticleListCount();
        PageResult<ArticleDTO> pageResult = new PageResult<>(pageCurrent, 3, rowCount);
        List<String> typeList = articleFilterDTO.getType();
        List<ArticleDTO> list = articleMapper.getAllArticleList(articleFilterDTO,typeList,pageResult);
        list = handleArticleDTOList(list);
        pageResult.setPageData(list);
        return pageResult;
    }
}
