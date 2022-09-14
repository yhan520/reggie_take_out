package com.wh95487.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wh95487.reggie.entity.Category;
import org.springframework.stereotype.Service;

public interface CategoryService extends IService<Category> {
    public void remove(Long ids);
}
