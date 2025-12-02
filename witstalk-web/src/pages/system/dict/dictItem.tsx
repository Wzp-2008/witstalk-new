
import { useState, useEffect } from 'react';
import {
    Table,
    Button,
    Form,
    Modal,
    Space,
    InputNumber,
    Input
} from 'antd';
import {
    PlusOutlined,
    EditOutlined,
    DeleteOutlined,
    EyeOutlined
} from '@ant-design/icons';
import { requestSWR, request } from '~/util/request';
import { showMessage } from '~/util/msg';
import WtDrawer from '~/components/WtDrawer';
import WtPagination from '~/components/WtPagination';

const url = {
    list: '/system/sysDictTypeItem/list',
    detail: '/system/sysDictTypeItem/detail',
    add: '/system/sysDictTypeItem/add',
    edit: '/system/sysDictTypeItem/update',
    delete: '/system/sysDictTypeItem/delete',
}

interface DictItemProps {
    dictTypeId: number;
    dictType: string;
    onClose: () => void;
}

export default function DictItem({ dictTypeId, dictType, onClose }: DictItemProps) {
    const [form] = Form.useForm();
    const [drawerVisible, setDrawerVisible] = useState(false);
    const [drawerTitle, setDrawerTitle] = useState('新增字典项');
    const [currentRecord, setCurrentRecord] = useState<any>(null);
    const [page, setPage] = useState(1);
    const [pageSize, setPageSize] = useState(10);
    const [total, setTotal] = useState(0);
    const [dataSource, setDataSource] = useState<any[]>([]);
    const [operationType, setOperationType] = useState<'add' | 'edit' | 'view'>('add');

    const columns = [
        {
            title: '#',
            dataIndex: 'index',
            key: 'index',
            align: 'center',
            width: 50,
            render: (_: string, __: any, index: number) => index + 1,
        },
        {
            title: '字典名称',
            dataIndex: 'dictName',
            key: 'dictName',
            align: 'center',
        },
        {
            title: '字典值',
            dataIndex: 'dictValue',
            key: 'dictValue',
            align: 'center',
        },
        {
            title: '排序',
            dataIndex: 'sort',
            key: 'sort',
            align: 'center',
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

    // 获取字典项列表数据
    const { data, error, mutate } = requestSWR({
        url: url.list,
        method: 'POST',
        data: {
            pageNumber: page,
            pageSize: pageSize,
            dictTypeId: dictTypeId,
        }
    });

    useEffect(() => {
        if (data) {
            setDataSource(data.records || []);
            setTotal(data.total || 0);
        }
    }, [data]);

    // 处理新增
    const handleAdd = () => {
        setOperationType('add');
        setDrawerTitle('新增字典项');
        setCurrentRecord(null);
        form.resetFields();
        setDrawerVisible(true);
    };

    // 处理编辑
    const handleEdit = (record: any) => {
        setOperationType('edit');
        setDrawerTitle('编辑字典项');
        setCurrentRecord(record);
        form.setFieldsValue(record);
        setDrawerVisible(true);
    };

    // 处理查看
    const handleView = (record: any) => {
        setOperationType('view');
        setDrawerTitle('查看字典项');
        setCurrentRecord(record);
        form.setFieldsValue(record);
        setDrawerVisible(true);
    };

    // 处理删除
    const handleDelete = (record: any) => {
        Modal.confirm({
            title: '确认删除',
            content: `确定要删除字典项【${record.dictName}】吗？`,
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
                    data: {
                        ...values,
                        dictTypeId: dictTypeId,
                        dictType: dictType
                    }
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
                <h3 className="text-lg font-bold">{dictType} - 字典项管理</h3>
                <Button type="primary" className='cream-button' icon={<PlusOutlined />} onClick={handleAdd}>
                    新增字典项
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
                        name="dictName"
                        label="字典名称"
                        rules={operationType !== 'view' ? [
                            { required: true, message: '请输入字典名称' }
                        ] : []}
                    >
                        <Input
                            className='cream-input'
                            placeholder="请输入字典名称"
                            disabled={operationType === 'view'}
                        />
                    </Form.Item>

                    <Form.Item
                        name="dictValue"
                        label="字典值"
                        rules={operationType !== 'view' ? [
                            { required: true, message: '请输入字典值' }
                        ] : []}
                    >
                        <Input
                            className='cream-input'
                            placeholder="请输入字典值"
                            disabled={operationType === 'view'}
                        />
                    </Form.Item>

                    <Form.Item
                        name="sort"
                        label="排序"
                        rules={operationType !== 'view' ? [
                            { required: true, message: '请输入排序' }
                        ] : []}
                    >
                        <InputNumber
                            className='cream-input'
                            placeholder="请输入排序"
                            disabled={operationType === 'view'}
                            min={0}
                            style={{ width: '100%' }}
                        />
                    </Form.Item>
                </Form>
            </WtDrawer>
        </div>
    )
}
