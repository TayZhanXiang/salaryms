package sg.com.hr.salaryms.dto;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class OffsetPageableDTO implements Pageable {

    private int offset;
    private int limit;
    private Sort sort;

    public OffsetPageableDTO() {
        super();
    }

    public OffsetPageableDTO(int offset, int limit, Sort sort) {
        this.offset = offset;
        this.limit = limit;
        this.sort = sort;
    }

    @Override
    public Pageable first() {
        return new OffsetPageableDTO(0, getPageSize(), getSort());
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }

    @Override
    public Pageable next() {
        return new OffsetPageableDTO((int) (getOffset() + getPageSize()), getPageSize(), getSort());
    }

    public Pageable previous() {
        return hasPrevious() ? new OffsetPageableDTO((int) (getOffset() - getPageSize()), getPageSize(), getSort())
                : this;
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new OffsetPageableDTO(pageNumber * getPageSize(), getPageSize(), getSort());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + limit;
        result = prime * result + offset;
        result = prime * result + ((sort == null) ? 0 : sort.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OffsetPageableDTO other = (OffsetPageableDTO) obj;
        if (limit != other.limit)
            return false;
        if (offset != other.offset)
            return false;
        if (sort == null) {
            if (other.sort != null)
                return false;
        } else if (!sort.equals(other.sort))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "OffsetPageableDTO [limit=" + limit + ", offset=" + offset + ", sort=" + sort + "]";
    }

}
