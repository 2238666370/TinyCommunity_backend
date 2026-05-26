package com.community.controller;

import com.community.entity.vo.CategoryVO;
import com.community.entity.vo.ResponseVO;
import com.community.service.CategoryInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName: BaseInfoController
 * Package: com.community.controller
 * Description:
 *
 * @Author wth
 * @Create 2026/3/18 17:51
 * @Version 1.0
 */
@RestController
@RequestMapping("/baseInfo")
@Slf4j
public class BaseInfoController extends ABaseController {
    @Resource
    private CategoryInfoService categoryInfoService;
    @RequestMapping("/getCategories")
    public ResponseVO getCategories(){
        List<CategoryVO> categories = categoryInfoService.getCategories();
        return this.getSuccessResponseVO(categories);
    }
}
