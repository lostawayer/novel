package com.rest;

import com.commons.mvc.WebResult;
import com.commons.data.DataPage;
import com.domain.Chapter;
import com.service.IChapterManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 章节管理Controller
 * 
 * @author Novel Platform Team
 * @date 2024-12-11
 */
@RestController
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@RequestMapping("/novel/chapter")
@Tag(name = "章节管理", description = "章节管理API")
public class ChapterController {

    @Autowired
    @Qualifier(IChapterManager.ID)
    private IChapterManager chapterManager;

    @PostMapping("/add")
    @Operation(summary = "添加章节", description = "添加或更新章节信息")
    public WebResult<Void> addChapter(@RequestBody Chapter chapter) {
        chapterManager.addOrUpdateChapter(chapter);
        return WebResult.createSuccessWebResult();
    }

    @GetMapping("/get")
    @Operation(summary = "获取章节", description = "根据ID获取章节信息")
    public WebResult<Chapter> getChapter(
            @RequestParam(value = "id") @Parameter(description = "章节ID") Long id) {
        Chapter chapter = chapterManager.getChapterById(id);
        if (chapter == null) {
            return WebResult.createFailureWebresult("章节不存在");
        }
        return WebResult.createSuccessWebResult(chapter);
    }

    @GetMapping("/list")
    @Operation(summary = "查询章节列表", description = "分页查询章节列表")
    public WebResult<DataPage<Chapter>> findChapters(
            @RequestParam(value = "pageNumber", defaultValue = "1") @Parameter(description = "页码") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") @Parameter(description = "每页数量") int pageSize,
            @RequestParam(value = "bookId", required = false) @Parameter(description = "小说ID") Long bookId,
            @RequestParam(value = "authorAccount", required = false) @Parameter(description = "作者账号") String authorAccount) {

        DataPage<Chapter> chapters = chapterManager.findChapters(pageNumber, pageSize, bookId, authorAccount);
        return WebResult.createSuccessWebResult(chapters);
    }

    @GetMapping("/listByBook")
    @Operation(summary = "查询小说的所有章节", description = "根据小说ID查询所有章节")
    public WebResult<List<Chapter>> findChaptersByBook(
            @RequestParam(value = "bookId") @Parameter(description = "小说ID") Long bookId) {
        List<Chapter> chapters = chapterManager.findChaptersByBookId(bookId);
        return WebResult.createSuccessWebResult(chapters);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除章节", description = "根据ID数组删除章节")
    public WebResult<Void> deleteChapter(
            @RequestParam(value = "ids") @Parameter(description = "章节ID数组") Long[] ids) {
        chapterManager.deleteChapter(ids);
        return WebResult.createSuccessWebResult();
    }
}
