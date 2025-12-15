package commons.data;


import com.commons.data.DataPage;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TestDataPage {
    @Test
    public void testCreateDataPage() {
        List<String> userList = new ArrayList<>();
        long pageNumber = 1;
        long pageSize = 10;
        long totalCount = 15;
        for (int i = 0; i < totalCount; i++) {
            userList.add("a"+i);
        }
        DataPage<String> dataPage=DataPage.createPage(userList,pageNumber,pageSize,totalCount);
        //System.out.println(dataPage);
        Assertions.assertEquals(2,dataPage.getTotalPage());
    }
    @Test
    public void testString(){
        Assertions.assertFalse(StringUtils.isNotBlank(null));
        Assertions.assertFalse(StringUtils.isNotBlank(""));
        Assertions.assertFalse(StringUtils.isNotBlank("   "));


    }
}
