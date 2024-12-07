package com.group4.rankingmanagementsystem.repository;

import com.group4.rankingmanagementsystem.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>,
        PagingAndSortingRepository<Role, Long>{
}
