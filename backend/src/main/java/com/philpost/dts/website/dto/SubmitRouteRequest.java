package com.philpost.dts.website.dto;

import java.util.List;

import com.philpost.dts.website.entity.RouteType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SubmitRouteRequest(@NotNull RouteType routeType,
                                 @NotEmpty @Size(max = 20) List<@Valid RouteStepRequest> steps,
                                 @Size(max = 1000) String remarks) {}