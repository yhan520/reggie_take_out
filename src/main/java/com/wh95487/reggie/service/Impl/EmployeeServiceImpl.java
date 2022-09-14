package com.wh95487.reggie.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wh95487.reggie.entity.Employee;
import com.wh95487.reggie.mapper.EmployeeMapper;
import com.wh95487.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService{
}
