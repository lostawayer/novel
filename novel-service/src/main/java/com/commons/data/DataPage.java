package com.commons.data;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class DataPage<T> implements Serializable {
    private  List<T>  data;
    private long totalCount;
    private long pageNumber;
    private long pageSize;
    private DataPage(){}
    public long getTotalPage() {
        return totalCount%pageSize==0?totalCount/pageSize:totalCount/pageSize+1;
    }
    public static  <T> DataPage<T> createPage(List<T> data,long pageNumber,long pageSize,long totalCount){
        DataPage<T> page = new DataPage<T>();
        page.setPageSize(pageSize);
        page.setPageNumber(pageNumber);
        page.setData(data);
        page.setTotalCount(totalCount);

        return page;
    }
}
