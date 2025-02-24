package com.ticketgo.request;

import com.ticketgo.enums.SortDirection;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@ToString
public class BasePageRequest {
    @Min(value = 1, message = "Page number phải lớn hơn hoặc bằng 0")
    private int pageNumber;
    @Min(value = 1, message = "Page size phải lớn hơn hoặc bằng 1")
    @Max(value = 50, message = "Page size phải nhỏ hơn hoặc bằng 50")
    private int pageSize;

    private String sortBy;
    private SortDirection sortDirection;

    public Sort buildSort() {
        if(sortBy == null) {
            this.sortBy = "createdAt";
        }
        return Sort.by(
                sortDirection == SortDirection.ASC
                ? Sort.Direction.ASC
                : Sort.Direction.DESC, sortBy
        );
    }
}
