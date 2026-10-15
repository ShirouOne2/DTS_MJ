package com.philpost.dts.website.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.philpost.dts.website.entity.ActionCode;

public interface ActionCodeRepository extends JpaRepository<ActionCode, String> {}