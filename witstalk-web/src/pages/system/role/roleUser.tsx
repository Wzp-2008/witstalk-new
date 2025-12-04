import React, { useState, useEffect } from 'react';
import {
    Button,
    Table,
    Spin,
    Empty,
    Modal
} from 'antd';
import {
    SaveOutlined,
    DeleteOutlined
} from '@ant-design/icons';
import { request } from '~/util/request';
import { showMessage } from '~/util/msg';
import SelectUser from '~/components/SelectUser';
import { type ColumnsType } from 'antd/es/table';

interface User {
    id: number;
    userName: string;
    nickName: string;
    email: string;
    createBy: string;
    createTime: string;
}

interface RoleUserBindingProps {
    roleId: number;
    roleName: string;
    visible: boolean;
    onClose: () => void;
    onSaveSuccess: () => void;
}

const url = {
    // 角色用户绑定接口
    roleUserList: '/system/sysRoleUser/getUserInfoByRoleId',
    saveRoleUser: '/system/sysRoleUser/save',
    removeRoleUser: '/system/sysRoleUser/delete'
};

const RoleUser: React.FC<RoleUserBindingProps> = ({
    roleId,
    roleName,
    visible,
    onClose,
    onSaveSuccess
}) => {
    const [selectedUsers, setSelectedUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState(false);
    const [saveLoading, setSaveLoading] = useState(false);
    const [deleteLoading, setDeleteLoading] = useState(false);

    // 获取角色已绑定的用户列表
    const getRoleUsers = async () => {
        try {
            setLoading(true);
            const response = await request({
                url: `${url.roleUserList}?roleId=${roleId}`,
                method: 'POST',
                data: {
                    roleId,
                }
            });
            setSelectedUsers(response?.data || []);
        } catch {
            showMessage.error('获取角色用户列表失败');
        } finally {
            setLoading(false);
        }
    };

    // 组件挂载或角色id变化时获取数据
    useEffect(() => {
        if (visible) {
            getRoleUsers();
        }
    }, [visible, roleId]);

    // 处理用户选择
    const handleUserSelect = (users: User[]) => {
        setSelectedUsers(users);
    };

    // 处理删除用户
    const handleDeleteUser = (id: number) => {
        Modal.confirm({
            title: '确认删除',
            content: '确定要移除该用户吗？',
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk: async () => {
                try {
                    setDeleteLoading(true);
                    await request({
                        url: `${url.removeRoleUser}?id=${id}`,
                        method: 'POST'
                    });
                    showMessage.success('删除成功');
                    getRoleUsers();
                } catch {
                    showMessage.error('删除失败');
                } finally {
                    setDeleteLoading(false);
                }
            }
        });
    };

    // 保存角色用户绑定
    const handleSaveRoleUser = async () => {
        try {
            setSaveLoading(true);
            // 构建保存数据
            const saveData = {
                roleId,
                userIds: selectedUsers.map(user => user.id)
            };

            await request({
                url: url.saveRoleUser,
                method: 'POST',
                data: saveData
            });

            showMessage.success('保存成功');
            onSaveSuccess();
            onClose();
        } catch {
            showMessage.error('保存失败');
        } finally {
            setSaveLoading(false);
        }
    };

    // 表格列配置
    const columns: ColumnsType = [
        {
            title: '用户名',
            dataIndex: 'username',
            key: 'username',
            align: 'center'
        },
        {
            title: '昵称',
            dataIndex: 'nickName',
            key: 'nickName',
            align: 'center'
        },
        {
            title: '邮箱',
            dataIndex: 'email',
            key: 'email',
            align: 'center'
        },
        {
            title: '操作',
            key: 'action',
            align: 'center',
            width: 100,
            render: (_: unknown, record: User) => (
                <Button
                    type="link"
                    danger
                    icon={<DeleteOutlined />}
                    onClick={() => handleDeleteUser(record.id)}
                    loading={deleteLoading}
                />
            )
        }
    ];

    return (
        <div className="p-4">
            <h4 className="mb-4 font-semibold">{roleName} - 用户绑定</h4>

            {/* 用户选择区域 */}
            <div className="bg-white p-4 rounded border mb-4">
                <h5 className="mb-2 font-medium">选择用户</h5>
                <SelectUser
                    value={selectedUsers}
                    onChange={handleUserSelect}
                    mode="multiple"
                    className="w-full"
                    placeholder="请选择要绑定的用户"
                />
            </div>

            {/* 已绑定用户列表 */}
            <div className="bg-white p-4 rounded border mb-4">
                <h5 className="mb-2 font-medium">已绑定用户</h5>
                {loading ? (
                    <div className="flex justify-center items-center h-[200px]">
                        <Spin size="large" />
                    </div>
                ) : selectedUsers.length > 0 ? (
                    <Table
                        bordered
                        columns={columns}
                        dataSource={selectedUsers}
                        rowKey="id"
                        pagination={{ pageSize: 10 }}
                        size="small"
                    />
                ) : (
                    <div className="flex justify-center items-center h-[200px]">
                        <Empty description="暂无绑定用户" />
                    </div>
                )}
            </div>

            {/* 操作按钮 */}
            <div className="flex justify-end gap-2">
                <Button onClick={onClose} className="cream-button">
                    取消
                </Button>
                <Button
                    type="primary"
                    icon={<SaveOutlined />}
                    onClick={handleSaveRoleUser}
                    loading={saveLoading}
                    className="cream-button"
                >
                    保存
                </Button>
            </div>
        </div>
    );
};

export default RoleUser;
