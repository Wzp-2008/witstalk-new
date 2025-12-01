import React from 'react';
import { Pagination } from 'antd';

interface WtPaginationProps {
  current: number;
  pageSize: number;
  total: number;
  onChange: (page: number, pageSize: number) => void;
  showSizeChanger?: boolean;
  pageSizeOptions?: string[];
  showTotal?: (total: number) => React.ReactNode;
  className?: string;
}

export default function WtPagination({
  current,
  pageSize,
  total,
  onChange,
  showSizeChanger = true,
  pageSizeOptions = ['10', '20', '50', '100'],
  showTotal = (total) => `共 ${total} 条记录`,
  className = ''
}: WtPaginationProps) {
  return (
    <div className={`flex justify-end ${className}`}>
      <Pagination
        current={current}
        pageSize={pageSize}
        total={total}
        onChange={onChange}
        showSizeChanger={showSizeChanger}
        pageSizeOptions={pageSizeOptions}
        showTotal={showTotal}
      />
    </div>
  );
}