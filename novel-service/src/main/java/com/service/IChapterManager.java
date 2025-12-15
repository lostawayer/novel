package com.service;

import com.commons.data.DataPage;
import com.domain.Chapter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 章节管理服务接口
 * 
 * @author Novel Platform Team
 * @date 2024-12-11
 */
@Validated
public interface IChapterManager {
    String ID = "chapterManager";

    /**
     * 添加或更新章节
     */
    void addOrUpdateChapter(@Valid Chapter chapter);

    /**
     * 根据ID获取章节
     */
    Chapter getChapterById(@NotNull Long id);

    /**
     * 删除章节
     */
    void deleteChapter(Long[] ids);

    /**
     * 分页查询章节
     */
    DataPage<Chapter> findChapters(int pageNumber, int pageSize, Long bookId, String authorAccount);

    /**
     * 根据小说ID查询所有章节（按序号排序）
     */
    List<Chapter> findChaptersByBookId(@NotNull Long bookId);
}
