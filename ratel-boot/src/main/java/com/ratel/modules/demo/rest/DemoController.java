package com.ratel.modules.demo.rest;

import com.ratel.framework.annotation.aop.log.RatelLog;
import com.ratel.framework.domain.validation.Group;
import com.ratel.framework.exception.BadRequestException;
import com.ratel.modules.demo.domain.DemoDomain;
import com.ratel.modules.demo.service.DemoService;
import com.ratel.modules.demo.service.dto.DemoQueryCriteria;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "实例")
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private static final String ENTITY_NAME = "demoDomain";

    @Autowired
    private DemoService demoService;

    /**
     * http://localhost:8000/api/demo?page=1&size=10&sort=id,username,desc
     *
     * @param criteria
     * @param pageable
     * @return
     */
    @RatelLog("实例查询-分页")
    @ApiOperation(value = "实例查询-分页")
    @GetMapping
    //@PreAuthorize("@el.check('demo:list')") //权限
    public ResponseEntity<Object> getDemoDomains(DemoQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(demoService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    /**
     * http://localhost:8000/api/demo/postParamUsers?page=1&size=10&sort=id,username,desc
     * body参数 {"id":1}
     *
     * @param criteria
     * @param pageable
     * @return
     */
    @RatelLog("实例查询-分页")
    @ApiOperation(value = "实例查询-分页")
    @PostMapping(value = "/postParamDemos")
    //@PreAuthorize("@el.check('demo:list')") //权限
    public ResponseEntity<Object> postParamDemos(@RequestBody DemoQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(demoService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @RatelLog("实例查询-查询信息")
    @ApiOperation(value = "实例查询-查询信息")
    @PostMapping(value = "/postDemoDomain")
    //@PreAuthorize("@el.check('demo:list')") //权限
    public ResponseEntity<DemoDomain> postDemoDomain(@RequestBody DemoDomain demoDomain) {
        return new ResponseEntity<>(demoService.getDemoDomain(demoDomain), HttpStatus.OK);
    }

    @RatelLog("实例查询")
    @ApiOperation("实例查询-查询信息")
    @GetMapping(value = "/getDemoDomainById/{id}")
    //@PreAuthorize("@el.check('demo:list')") //权限
    public ResponseEntity<Object> getDemoDomainById(@PathVariable Long id) {
        return new ResponseEntity<>(demoService.findOne(id), HttpStatus.OK);
    }

    @RatelLog("新增实例")
    @ApiOperation("新增实例")
    @PostMapping(value = "/addDemoDomain")
    //@PreAuthorize("@el.check('demo:add')")  //权限
    public ResponseEntity<Object> create(@Validated({Group.class}) @RequestBody DemoDomain resources) {
        if (resources.getId() != null) {
            throw new BadRequestException("A new " + ENTITY_NAME + " cannot already have an ID");
        }
        demoService.save(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RatelLog("修改实例")
    @ApiOperation(value = "修改实例")
    @PutMapping
    @PreAuthorize("@el.check('demo:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DemoDomain resources) {
        demoService.save(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RatelLog("删除实例-物理删除")
    @ApiOperation(value = "删除实例-物理删除")
    @DeleteMapping
    @PreAuthorize("@el.check('demo:del')")
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        demoService.deleteBatch(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RatelLog("删除实例-逻辑删除")
    @ApiOperation(value = "删除实例-逻辑删除")
    @PostMapping(value = "/updateStatus")
    //@PreAuthorize("@el.check('demo:del')")
    public ResponseEntity<Object> updateStatus(@RequestBody Long[] ids) {
        demoService.updateStatus(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RatelLog("更新一个例子")
    @ApiOperation(value = "更新一个例子")
    @PostMapping(value = "/updateNickName")
    //@PreAuthorize("@el.check('demo:del')")
    public ResponseEntity<Object> updateNickName(@RequestBody DemoDomain resources) {
        demoService.updateNickName(resources);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
