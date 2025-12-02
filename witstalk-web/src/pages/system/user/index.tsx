import { useState, useEffect } from 'react';
import {
    Table,
    Button,
    Input,
    Form,
    Modal,
    Space,
    Avatar,
    Upload,
    message
} from 'antd';
import {
    PlusOutlined,
    EditOutlined,
    DeleteOutlined,
    EyeOutlined,
    SearchOutlined,
    UploadOutlined
} from '@ant-design/icons';
import { requestSWR, request } from '~/util/request';
import { showMessage } from '~/util/msg';
import WtDrawer from '~/components/WtDrawer';
import WtPagination from '~/components/WtPagination';
import defaultAvatar from '~/assets/images/defaultAvatar.svg';
import type {ColumnsType} from "antd/es/table";


const url = {
    list: '/system/sysUser/list',
    add: '/system/sysUser/add',
    detail: '/system/sysUser/detail',
    delete: '/system/sysUser/delete',
    edit: '/system/sysUser/update'
}

export default function User() {
    const [form] = Form.useForm();
    const [searchText, setSearchText] = useState('');
    const [drawerVisible, setDrawerVisible] = useState(false);
    const [drawerTitle, setDrawerTitle] = useState('新增用户');
    const [currentRecord, setCurrentRecord] = useState<any>(null);
    const [page, setPage] = useState(1);
    const [pageSize, setPageSize] = useState(10);
    const [total, setTotal] = useState(0);
    const [dataSource, setDataSource] = useState<any[]>([]);
    const [operationType, setOperationType] = useState<'add' | 'edit' | 'view'>('add');

    const columns: ColumnsType = [
        {
            title: '#',
            dataIndex: 'index',
            key: 'index',
            align: 'center',
            width: 50,
            render: (_: string, __: any, index: number) => index + 1,
        },
        {
            title: '用户名',
            dataIndex: 'username',
            key: 'username',
            align: 'center',
        },
        {
            title: '昵称',
            dataIndex: 'nickName',
            key: 'nickName',
            align: 'center',
        },
        {
            title: '邮箱',
            dataIndex: 'email',
            key: 'email',
            align: 'center',
        },
        {
            title: '头像',
            dataIndex: 'avatar',
            key: 'avatar',
            align: 'center',
            render: (avatar: string) => (
                <Avatar src={avatar ? `data:image/jpeg;base64,${avatar}` : defaultAvatar} />
            ),
        },
        {
            title: '创建人',
            dataIndex: 'createBy',
            key: 'createBy',
            align: 'center',
            width: 100,
        },
        {
            title: '创建时间',
            dataIndex: 'createTime',
            key: 'createTime',
            align: 'center',
            width: 200,
        },
        {
            title: '修改人',
            dataIndex: 'updateBy',
            key: 'updateBy',
            align: 'center',
            width: 100,
        },
        {
            title: '修改时间',
            dataIndex: 'updateTime',
            key: 'updateTime',
            align: 'center',
            width: 200,
        },
        {
            title: '操作',
            key: 'action',
            fixed: 'end',
            width: 150,
            align: 'center',
            render: (_: any, record: any) => (
                <Space size="middle">
                    <Button type="link" icon={<EyeOutlined />} onClick={() => handleView(record)} />
                    <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)} />
                    <Button type="link" danger icon={<DeleteOutlined />} onClick={() => handleDelete(record)} />
                </Space>
            ),
        },
    ]

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

    useEffect(() => {
        if (data) {
            setDataSource(data.records || []);
            setTotal(data.total || 0);
        }
    }, [data]);

    // 处理搜索
    const handleSearch = () => {
        setPage(1);
        mutate();
    };

    // 处理重置
    const handleReset = () => {
        setSearchText('');
        setPage(1);
        mutate();
    };

    // 处理新增
    const handleAdd = () => {
        setOperationType('add');
        setDrawerTitle('新增用户');
        setCurrentRecord(null);
        form.resetFields();
        setDrawerVisible(true);
    };

    // 处理编辑
    const handleEdit = (record: any) => {
        setOperationType('edit');
        setDrawerTitle('编辑用户');
        setCurrentRecord(record);
        form.setFieldsValue(record);
        setDrawerVisible(true);
    };

    // 处理查看
    const handleView = (record: any) => {
        setOperationType('view');
        setDrawerTitle('查看用户');
        setCurrentRecord(record);
        form.setFieldsValue(record);
        setDrawerVisible(true);
    };

    // 处理删除
    const handleDelete = (record: any) => {
        Modal.confirm({
            title: '确认删除',
            content: `确定要删除用户【${record.username}】吗？`,
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk: async () => {
                try {
                    await request({
                        url: url.delete,
                        method: 'POST',
                        params: {
                            id: record.id
                        }
                    });
                    showMessage.success('删除成功');
                    mutate();
                } catch (error) {
                    showMessage.error('删除失败');
                }
            }
        });
    };

    // 将图片转换为去头的base64
    const getBase64 = (file: File): Promise<string> => {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.readAsDataURL(file);
            reader.onload = () => {
                const base64Str = reader.result as string;
                // 去掉base64头部，只保留数据部分
                const headerlessBase64 = base64Str.split(',')[1];
                resolve(headerlessBase64);
            };
            reader.onerror = error => reject(error);
        });
    };

    // 处理头像上传
    const handleAvatarUpload = async (file: File) => {
        // 验证文件类型
        const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
        if (!isJpgOrPng) {
            message.error('只能上传 JPG 或 PNG 格式的图片!');
            return false;
        }
        // 验证文件大小 (256KB)
        const isLt256K = file.size / 1024 < 256;
        if (!isLt256K) {
            message.error('图片大小不能超过 256KB!');
            return false;
        }
        // 转换为去头的base64
        const base64 = await getBase64(file);
        // 设置表单字段值
        form.setFieldValue('avatar', base64);
        return false; // 阻止自动上传
    };

    // 处理表单提交
    const handleSubmit = async () => {
        try {
            const values = await form.validateFields();
            if (currentRecord) {
                // 编辑
                await request({
                    url: url.edit,
                    method: 'POST',
                    data: {
                        ...values,
                        id: currentRecord.id
                    }
                });
                showMessage.success('编辑成功');
            } else {
                // 新增
                await request({
                    url: url.add,
                    method: 'POST',
                    data: values
                });
                showMessage.success('新增成功');
            }
            setDrawerVisible(false);
            mutate();
        } catch (error) {
            showMessage.error('操作失败');
        }
    };

    // 处理分页变化
    const handlePageChange = (page: number, pageSize: number) => {
        setPage(page);
        setPageSize(pageSize);
    };

    return (
        <div className="p-4">
            <div className="flex justify-between items-center mb-4">
                <h3 className="text-lg font-bold">用户管理</h3>
                <Button type="primary" className='cream-button' icon={<PlusOutlined />} onClick={handleAdd}>
                    新增用户
                </Button>
            </div>

            {/* 搜索区域 */}
            <div className="flex mb-4 gap-2">
                <Input
                    placeholder="请输入用户名或昵称"
                    value={searchText}
                    className='cream-input'
                    onChange={(e) => setSearchText(e.target.value)}
                    prefix={<SearchOutlined />}
                    style={{ width: 300 }}
                />
                <Button type="primary" className='cream-button' onClick={handleSearch}>
                    搜索
                </Button>
                <Button className='cream-button' onClick={handleReset}>
                    重置
                </Button>
            </div>

            {/* 表格区域 */}
            <Table
                bordered
                columns={columns}
                dataSource={dataSource}
                rowKey="id"
                pagination={false}
                loading={!data && !error}
            />

            {/* 分页区域 */}
            <WtPagination
                current={page}
                pageSize={pageSize}
                total={total}
                onChange={handlePageChange}
                className="mt-4"
            />

            {/* 新增/编辑/查看抽屉 */}
            <WtDrawer
                title={drawerTitle}
                open={drawerVisible}
                onClose={() => setDrawerVisible(false)}
                onOk={operationType !== 'view' ? form.submit : undefined}
                okButtonProps={{ className: 'cream-button' }}
                cancelButtonProps={{ className: 'cream-button' }}
            >
                <Form
                    form={form}
                    layout="vertical"
                    onFinish={handleSubmit}
                >
                    <Form.Item
                        name="username"
                        label="用户名"
                        rules={operationType !== 'view' ? [
                            { required: true, message: '请输入用户名' }
                        ] : []}
                    >
                        <Input
                            className='cream-input'
                            placeholder="请输入用户名"
                            disabled={operationType === 'view'}
                        />
                    </Form.Item>

                    <Form.Item
                        name="nickName"
                        label="昵称"
                        rules={operationType !== 'view' ? [
                            { required: true, message: '请输入昵称' }
                        ] : []}
                    >
                        <Input
                            className='cream-input'
                            placeholder="请输入昵称"
                            disabled={operationType === 'view'}
                        />
                    </Form.Item>

                    <Form.Item
                        name="email"
                        label="邮箱"
                        rules={operationType !== 'view' ? [
                            { required: true, message: '请输入邮箱' },
                            { type: 'email', message: '请输入有效的邮箱地址' }
                        ] : []}
                    >
                        <Input
                            className='cream-input'
                            placeholder="请输入邮箱"
                            disabled={operationType === 'view'}
                        />
                    </Form.Item>

                    {
                        operationType === 'add' &&
                        <Form.Item
                            name="password"
                            label="密码"
                            rules={operationType === 'add' ? [
                                { required: true, message: '请输入密码' },
                                {
                                    pattern: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{6,20}$/,
                                    message: '密码至少6位，必须包含字母和数字'
                                }
                                ] : []}
                        >
                            <Input.Password
                                className='cream-input'
                                placeholder={operationType === 'add' ? '请输入密码' : '不修改密码请留空'}
                            />
                        </Form.Item>
                    }

                    <Form.Item
                        name="avatar"
                        label="头像"
                    >
                        <div className="flex items-center gap-4">
                            {/* 显示当前头像 */}
                            <Avatar
                                src={form.getFieldValue('avatar') ? `data:image/jpeg;base64,${form.getFieldValue('avatar')}` : undefined}
                                size={100}
                            />
                            {/* 上传按钮（仅在非查看模式显示） */}
                            {operationType !== 'view' && (
                                <Upload
                                    listType="picture-card"
                                    beforeUpload={handleAvatarUpload}
                                    showUploadList={false}
                                >
                                    <Button icon={<UploadOutlined />} className='cream-button'>
                                        上传头像
                                    </Button>
                                </Upload>
                            )}
                        </div>
                    </Form.Item>
                </Form>
            </WtDrawer>
        </div>
    );
}
