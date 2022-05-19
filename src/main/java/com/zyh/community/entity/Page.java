package com.zyh.community.entity;

/**
 * @author
 * @Description 封装分页的相关信息
 * @create 2022-05-16 20:27
 */
public class Page {
    //当前页
    private int current = 1;
    //每页显示上限
    private int limit = 10;
    //数据总条数
    private int rows;
    //访问路径
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current>=1)
            this.current = current;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit>=1 && limit<=100)
            this.limit = limit;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows>=1)
            this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    //获取当前页起始行
    public int getOffset(){
        return limit*(current-1);
    }
    //获取总页数
    public int getTotal(){
        return rows%limit == 0?rows/limit:rows/limit+1;
    }

    //起始页码
    public int getFrom(){
        return current-2>0?current-2:1;
    }
    //当前页码
    public int getTo(){
        int total = getTotal();
        int post = current+2;
        return post<total?post:total;
    }
}
