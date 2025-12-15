package com.service.normal;

import com.commons.data.DataPage;
import com.domain.Chapter;
import com.persistence.mybatis.mapper.IChapterMapper;
import com.service.IChapterManager;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 章节管理服务实现
 * 
 * @author Novel Platform Team
 * @date 2024-12-11
 */
@Service(IChapterManager.ID)
@CommonsLog
public class ChapterManager implements IChapterManager {

    @Autowired
    private IChapterMapper chapterMapper;

    @Override
    @Transactional
    public void addOrUpdateChapter(Chapter chapter) {
        if (chapter.getId() == null) {
            // 新增
            chapter.setCreateTime(new Date());
            chapterMapper.insert(chapter);
            log.info("添加章节成功：" + chapter.getChapterTitle());
        } else {
            // 更新
            chapterMapper.updateById(chapter);
            log.info("更新章节成功：" + chapter.getChapterTitle());
        }
    }

    @Override
    public Chapter getChapterById(Long id) {
        return chapterMapper.selectById(id);
    }

    @Override
    @Transactional
    public void deleteChapter(Long[] ids) {
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                chapterMapper.deleteById(id);
            }
            log.info("删除章节数量：" + ids.length);
        }
    }

    @Override
    public DataPage<Chapter> findChapters(int pageNumber, int pageSize, Long bookId, String authorAccount) {
        int offset = (pageNumber - 1) * pageSize;
        List<Chapter> chapters = chapterMapper.selectPage(offset, pageSize, bookId, null);
        long total = chapterMapper.selectCount(bookId, null);

        return DataPage.createPage(chapters, pageNumber, pageSize, total);
    }

    @Override
    public List<Chapter> findChaptersByBookId(Long bookId) {
        return chapterMapper.selectByBookId(bookId);
    }
}
