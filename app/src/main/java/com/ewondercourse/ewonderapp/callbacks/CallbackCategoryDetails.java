package com.ewondercourse.ewonderapp.callbacks;

import com.ewondercourse.ewonderapp.models.Category;
import com.ewondercourse.ewonderapp.models.News;

import java.util.ArrayList;
import java.util.List;

public class CallbackCategoryDetails {

    public String status = "";
    public int count = -1;
    public int count_total = -1;
    public int pages = -1;
    public Category category = null;
    public List<News> posts = new ArrayList<>();

}
