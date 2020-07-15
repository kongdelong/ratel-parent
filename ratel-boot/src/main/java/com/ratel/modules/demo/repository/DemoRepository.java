package com.ratel.modules.demo.repository;

import com.ratel.framework.repository.BaseRepository;
import com.ratel.modules.demo.domain.DemoDomain;
import com.ratel.modules.demo.service.vo.DemoDomainVo;
import com.ratel.modules.system.domain.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface DemoRepository extends BaseRepository<DemoDomain, Long> {


    @Modifying
    @Query(value = "update DemoDomain set username='0' where id=?1")
    void updateStatus(Long id);


    @Modifying
    @Query(value = "update DemoDomain set nickName =:#{#demoDomain.nickName}  where id =:#{#demoDomain.id} ")
    void updateNickName(DemoDomain demoDomain);


    //-------jpql查询--------

    //根据用户id和firstName查询
    @Query(value = "from DemoDomain where id = ?2 and firstName = ?1")
    SysUser findUserByIdAndName(String username, int id);

    //动态条件查询
    @Query(value = "from DemoDomain where (?1 is null or username=?1) and (?2 is null or id=?2)")
    List<DemoDomain> findUserBySql(String username, String id, Pageable pageable);

    //联合查询
    //@Query(value = "SELECT new com.ratel.modules.demo.service.vo.DemoDomainVo( b.teacherIdCard,b.teacherName,b.sex,b.highestDegree,b.specializedTechnicalJob,b.teacherStatus,c.schoolName) FROM ScheduleTeacher a  inner JOIN Teacher b on a.teacher.id=b.id LEFT JOIN School c on b.school.id=c.id WHERE a.schedule.id= :scheduleId")
    @Query(value = "SELECT new com.ratel.modules.demo.service.vo.DemoDomainVo( a.age,b.username,b.nickName,b.sysDept)" +
            " FROM DemoDomain a inner JOIN SysUser b on a.deptId=b.sysDept.id WHERE a.id= :id")
    List<DemoDomainVo> getDemoDomainVo(@Param("id") Long id, Pageable pageable);


    @Query("select s from DemoDomain s where s.id = :id")
    Page<SysUser> selectAllByUserId(@Param("id") Long userId, Pageable pageable);

    //-------sql查询--------

    @Query(value = "select * from user where nick_name like ?", nativeQuery = true)
    List<SysUser> sqlFindByName(String nickName);

    @Query(value = "SELECT * FROM user WHERE nick_name = ?1",
            countQuery = "SELECT count(*) FROM user WHERE nick_name = ?1",
            nativeQuery = true)
    Page<SysUser> findByLastname(String nickName, Pageable pageable);


    //-------方法命名查询--------

    //排序
    List<SysUser> findByUsername(String username, Sort sort);

    //根据firstName与LastName查找(两者必须在数据库有)
    SysUser findByUsernameAndNickName(String usename, String nickName);

    //根据firstName或LastName查找(两者其一有就行)
    SysUser findByUsernameOrNickName(String usename, String nickName);

    //根据firstName查找它是否存在数据库里<类似与以下关键字>
    //User findByUsername(String usename);
    SysUser findByUsernameIs(String usename);

}
