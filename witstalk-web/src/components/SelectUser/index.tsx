
import React, { useState, useEffect } from 'react';
import {
  Select,
  Spin,
  Avatar,
  List,
  Input,
  Pagination,
  Checkbox,
  Space
} from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import { requestSWR } from '~/util/request';

/*
<Form.Item
  name="userId"
  label="负责人"
  rules={[{ required: true, message: '请选择负责人' }]}
>
  <SelectUser placeholder="请选择负责人" />
</Form.Item>

<Form.Item
  name="userIds"
  label="参与人员"
  rules={[{ required: true, message: '请选择参与人员' }]}
>
  <SelectUser mode="multiple" placeholder="请选择参与人员" />
</Form.Item>

// 单选模式
<SelectUser
  value={selectedUserId}
  onChange={(value) => setSelectedUserId(value as number)}
  placeholder="请选择人员"
/>

// 多选模式
<SelectUser
  mode="multiple"
  value={selectedUserIds}
  onChange={(value) => setSelectedUserIds(value as number[])}
  placeholder="请选择人员"
  maxTagCount={2}
/>
*/

const { Option } = Select;

const url = {
  list: '/system/sysUser/list',
}

interface User {
  id: number;
  username: string;
  nickName: string;
  avatar: string;
  email: string;
  userId?: number;
}

interface SelectUserProps {
  value?: number | number[] | User | User[];
  onChange?: (value: User[]) => void;
  placeholder?: string;
  mode?: 'single' | 'multiple';
  style?: React.CSSProperties;
  className?: string;
  disabled?: boolean;
  maxTagCount?: number;
}

export default function SelectUser({
  value,
  onChange,
  placeholder = '请选择人员',
  mode = 'single',
  style,
  className,
  disabled = false,
  maxTagCount = 3
}: SelectUserProps) {
  const [searchText, setSearchText] = useState('');
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [selectedUserIds, setSelectedUserIds] = useState<number[]>([]);
  const [open, setOpen] = useState(false); // 手动控制下拉框的显示和隐藏

  // 初始化选中值
  useEffect(() => {
    if (value) {
      if (Array.isArray(value)) {
        // 多选模式
        if (value.length > 0 && typeof value[0] === 'object') {
          // 如果是User对象数组
          setSelectedUserIds(value.map(user => (user as User).userId!));
        } else {
          // 如果是number数组
          setSelectedUserIds(value as number[]);
        }
      } else {
        // 单选模式
        if (typeof value === 'object') {
          // 如果是User对象
          setSelectedUserIds([value.userId!]);
        } else {
          // 如果是number
          setSelectedUserIds(value ? [value] : []);
        }
      }
    } else {
      setSelectedUserIds([]);
    }
  }, [value]);

  // 获取用户列表数据
  const { data, error, mutate } = requestSWR({
    url: url.list,
    method: 'POST',
    data: {
      pageNumber: page,
      pageSize: pageSize,
      username: searchText,
      nickName: searchText
    }
  });

  const users = data?.records as User[] || [];
  const total = data?.total || 0;

  // 处理搜索
  const handleSearch = (value: string) => {
    setSearchText(value);
    setPage(1);
    mutate();
  };

  // 处理分页变化
  const handlePageChange = (page: number, pageSize: number) => {
    setPage(page);
    setPageSize(pageSize);
  };

  // 处理用户选择
  const handleUserSelect = (userId: number) => {
    let newSelectedUserIds: number[];
    if (mode === 'multiple') {
      // 多选模式
      if (selectedUserIds.includes(userId)) {
        newSelectedUserIds = selectedUserIds.filter(id => id !== userId);
      } else {
        newSelectedUserIds = [...selectedUserIds, userId];
      }
    } else {
      // 单选模式
      newSelectedUserIds = [userId];
    }
    setSelectedUserIds(newSelectedUserIds);

    if (onChange) {
      // 获取选中的用户对象
      const selectedUsers = users.filter(user => newSelectedUserIds.includes(user.id));

      if (mode === 'multiple') {
        // 多选模式返回User对象数组
        onChange(selectedUsers);
      } else {
        // 单选模式返回单个User对象或undefined
        onChange(selectedUsers[0] || undefined);
      }
    }
  };

  // 自定义下拉菜单内容
  const dropdownRender = () => (
    <div style={{ width: 360, padding: 12, boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)', borderRadius: '4px' }}>
      {/* 搜索框 */}
      <Input
        placeholder="搜索用户名或昵称"
        prefix={<SearchOutlined />}
        onChange={(e) => handleSearch(e.target.value)}
        style={{ marginBottom: 12, borderRadius: '4px' }}
        onClick={(e) => e.stopPropagation()} // 阻止点击事件冒泡，防止下拉菜单关闭
      />

      {/* 加载状态 */}
      {!data && !error ? (
        <div style={{ display: 'flex', justifyContent: 'center', padding: '32px 0' }}>
          <Spin />
        </div>
      ) : error ? (
        <div style={{ textAlign: 'center', padding: '32px 0', color: '#ff4d4f' }}>加载失败</div>
      ) : users.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '32px 0', color: '#bfbfbf' }}>暂无数据</div>
      ) : (
        <>
          {/* 用户列表 */}
          <List
            dataSource={users}
            renderItem={(user: User) => (
              <List.Item
                key={user.id}
                style={{
                  cursor: 'pointer',
                  borderRadius: '4px',
                  marginBottom: '4px',
                  transition: 'all 0.3s'
                }}
                className="hover:bg-gray-50"
                onClick={(e) => {
                  e.stopPropagation(); // 阻止点击事件冒泡
                  handleUserSelect(user.id);
                }}
              >
                <List.Item.Meta
                  avatar={<Avatar
                    src={user.avatar ? `data:image/jpeg;base64,${user.avatar}` : undefined}
                    size={32}
                  />}
                  title={
                    <Space size="middle" style={{ alignItems: 'center' }}>
                      <span style={{ fontWeight: '500', fontSize: '14px' }}>{user.nickName || user.username}</span>
                      <Checkbox
                        checked={selectedUserIds.includes(user.id)}
                        onChange={(e) => {
                          e.stopPropagation(); // 阻止点击事件冒泡
                          handleUserSelect(user.id);
                        }}
                      />
                    </Space>
                  }
                  description={<span style={{ fontSize: '12px', color: '#8c8c8c' }}>{user.email}</span>}
                />
              </List.Item>
            )}
            style={{ maxHeight: 320, overflowY: 'auto', marginBottom: '12px' }}
            className="user-list"
          />

          {/* 分页 */}
          <div style={{ display: 'flex', justifyContent: 'flex-end', alignItems: 'center' }}>
            <Pagination
              current={page}
              pageSize={pageSize}
              total={total}
              onChange={(page, pageSize) => {
                handlePageChange(page, pageSize);
              }}
              showSizeChanger
              pageSizeOptions={['5']}
              showTotal={(total) => `共 ${total} 条记录`}
              style={{ margin: 0 }}
            />
          </div>
        </>
      )}
    </div>
  );

  // 从value中提取出userId，用于Select组件的value属性
  const getSelectValue = () => {
    if (value) {
      if (Array.isArray(value)) {
        // 多选模式
        if (value.length > 0 && typeof value[0] === 'object') {
          // 如果是User对象数组
          return value.map(user => (user as User).id);
        } else {
          // 如果是number数组
          return value as number[];
        }
      } else {
        // 单选模式
        if (typeof value === 'object') {
          // 如果是User对象
          return (value as User).id;
        } else {
          // 如果是number
          return value as number;
        }
      }
    }
    return undefined;
  };

  return (
    <Select
      value={getSelectValue()}
      onChange={() => {
        // 内部Select组件的onChange事件，这里我们主要依赖自定义的handleUserSelect处理
        // 但仍然需要监听这个事件，以确保受控组件的正确性
      }}
      placeholder={placeholder}
      mode={mode === 'multiple' ? 'multiple' : undefined}
      style={style}
      className={className}
      disabled={disabled}
      maxTagCount={maxTagCount}
      dropdownRender={dropdownRender}
      allowClear
      open={open} // 手动控制下拉框的显示和隐藏
      onDropdownVisibleChange={(visible) => {
        setOpen(visible); // 监听下拉框的显示状态变化
      }}
      // 使用 dropdownRender 配合 open 属性，确保下拉框不会意外关闭
      getPopupContainer={(triggerNode) => triggerNode.parentNode as HTMLElement}
    >
      {/* 动态生成选项 */}
      {users.map((user: User) => (
        <Option key={user.id} value={user.id}>
          <Space>
            <Avatar src={user.avatar ? `data:image/jpeg;base64,${user.avatar}` : undefined} size={20} />
            <span>{user.nickName || user.username}</span>
          </Space>
        </Option>
      ))}
    </Select>
  );
}
