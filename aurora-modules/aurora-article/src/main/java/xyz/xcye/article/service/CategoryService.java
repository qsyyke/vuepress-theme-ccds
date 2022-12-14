package xyz.xcye.article.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import xyz.xcye.article.po.Category;
import xyz.xcye.article.pojo.CategoryPojo;
import xyz.xcye.article.vo.CategoryVO;
import xyz.xcye.aurora.util.UserUtils;
import xyz.xcye.core.dto.JwtUserInfo;
import xyz.xcye.core.enums.ResponseStatusCodeEnum;
import xyz.xcye.core.exception.article.ArticleException;
import xyz.xcye.core.exception.user.UserException;
import xyz.xcye.core.util.BeanUtils;
import xyz.xcye.core.util.lambda.AssertUtils;
import xyz.xcye.data.entity.Condition;
import xyz.xcye.data.entity.PageData;
import xyz.xcye.data.util.PageUtils;

/**
 * @author qsyyke
 * @date Created in 2022/5/11 19:23
 */


@Service
public class CategoryService {

    @Autowired
    private AuroraCategoryService auroraCategoryService;

    public int deleteByPrimaryKey(Long uid) {
        Assert.notNull(uid, "uid不能为null");
        Category category = Category.builder()
                .uid(uid)
                .delete(true)
                .build();
        return auroraCategoryService.updateById(category);
    }

    public int physicsDeleteByUid(Long uid) {
        Assert.notNull(uid, "uid不能为null");
        return auroraCategoryService.deleteById(uid);
    }

    public void insertSelective(CategoryPojo record) {
        Assert.notNull(record, "类别不能为null");
        record.setDelete(false);
        JwtUserInfo jwtUserInfo = UserUtils.getCurrentUser();
        AssertUtils.stateThrow(jwtUserInfo != null,
                () -> new UserException(ResponseStatusCodeEnum.PERMISSION_USER_NOT_LOGIN));
        record.setUserUid(jwtUserInfo.getUserUid());
        auroraCategoryService.insert(BeanUtils.copyProperties(record, Category.class));
    }

    public PageData<CategoryVO> selectByCondition(Condition<Long> condition) {
        Assert.notNull(condition,"查询条件不能为null");
        return PageUtils.pageList(condition, t -> auroraCategoryService.queryListByCondition(condition), CategoryVO.class);
    }

    public CategoryVO selectByUid(Long uid) {
        Assert.notNull(uid, "uid不能为null");
        return BeanUtils.getSingleObjFromList(auroraCategoryService.queryListByCondition(Condition.instant(uid, true)), CategoryVO.class);
    }

    public CategoryVO selectByTitle(String title) {
        AssertUtils.stateThrow(StringUtils.hasLength(title), () -> new ArticleException("类别不能为null"));
        Category category = new Category();
        category.setTitle(title);
        return BeanUtils.copyProperties(auroraCategoryService.queryOne(category), CategoryVO.class);
    }

    public int updateByPrimaryKeySelective(CategoryPojo record) {
        Assert.notNull(record, "类别不能为null");
        record.setUserUid(null);
        return auroraCategoryService.updateById(BeanUtils.copyProperties(record, Category.class));
    }
}
