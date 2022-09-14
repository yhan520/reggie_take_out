package com.wh95487.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wh95487.reggie.common.BaseContext;
import com.wh95487.reggie.common.R;
import com.wh95487.reggie.entity.AddressBook;
import com.wh95487.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    //新增地址
    @PostMapping
    public R<AddressBook> addAddress(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);

        addressBookService.save(addressBook);

        return R.success(addressBook);
    }

    //设置默认地址
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        log.info("addressBook:{}", addressBook);
        //先将所有的地址都设置成非默认地址
        LambdaUpdateWrapper<AddressBook> lqw = new LambdaUpdateWrapper<>();
        lqw.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        lqw.set(AddressBook::getIsDefault, 0);
        addressBookService.update(lqw);

        //将传过来的地址设置成默认地址
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);

        return R.success(addressBook);
    }

    //根据ID查询地址
    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if(addressBook != null){
            return R.success(addressBook);
        }
        return R.error("查询失败");
    }

    //查询默认地址
    @GetMapping("default")
    public R<AddressBook> getDefaultAddress(){
        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AddressBook::getUserId, BaseContext.getCurrentId()).eq(AddressBook::getIsDefault, 1);
        AddressBook addressBook = addressBookService.getOne(lqw);

        if(addressBook != null)
            return R.success(addressBook);
        return R.error("查询失败");
    }

    //查询指定用户的全部地址
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);

        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        lqw.eq(addressBook.getUserId() != null, AddressBook::getUserId, addressBook.getUserId()).
                orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> addressBookList = addressBookService.list(lqw);

        return R.success(addressBookList);
    }
}
