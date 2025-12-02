import { useState, useEffect } from 'react';
import {
    Table,
    Button,
    Input,
    Form,
    Modal,
    Space
} from 'antd';
import {
    PlusOutlined,
    EditOutlined,
    DeleteOutlined,
    SearchOutlined,
    EyeOutlined,
    RedoOutlined
} from '@ant-design/icons';
import { requestSWR, request } from '~/util/request';
import { showMessage } from '~/util/msg';
import WtDrawer from '~/components/WtDrawer';
import WtPagination from '~/components/WtPagination';
import DictItem from './dictItem';
import type {ColumnsType} from "antd/es/table";

const url = {
    list: '/system/sysDictType/list',
    add: '/system/sysDictType/add',
    detail: '/system/sysDictType/detail',
    delete: '/system/sysDictType/delete',
    edit: '/system/sysDictType/update',
    refreshCache: '/system/sysDictType/refreshCache',
}

export default function Dict() {
    const [form] = Form.useForm();
    const [searchText, setSearchText] = useState('');
    const [drawerVisible, setDrawerVisible] = useState(false);
    const [drawerTitle, setDrawerTitle] = useState('新增字典类型');
    const [currentRecord, setCurrentRecord] = useState<any>(null);
    const [page, setPage] = useState(1);
    const [pageSize, setPageSize] = useState(10);
    const [total, setTotal] = useState(0);
    const [dataSource, setDataSource] = useState<any[]>([]);
    const [operationType, setOperationType] = useState<'add' | 'edit' | 'view'>('add');
    // 字典项抽屉状态
    const [dictItemDrawerVisible, setDictItemDrawerVisible] = useState(false);
    const [currentDictType, setCurrentDictType] = useState<{
        id: number;
        dictType: string;
    }>({ id: 0, dictType: '' });

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
            title: '字典类型',
            dataIndex: 'dictType',
            key: 'dictType',
            align: 'center',
            render: (text: string, record: any) => (
                <a onClick={() => {
                    setCurrentDictType({ id: record.id, dictType: record.dictType });
                    setDictItemDrawerVisible(true);
                }}>
                    {text}
                </a>
            ),
        },
        {
            title: '字典名称',
            dataIndex: 'dictName',
            key: 'dictName',
            align: 'center',
        },
        {
            title: '字典描述',
            dataIndex: 'dictDesc',
            key: 'dictDesc',
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
            width: 100,
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

    // 获取字典类型列表数据
    const { data, error, mutate } = requestSWR({
        url: url.list,
        method: 'POST',
        data: {
            pageNumber: page,
            pageSize: pageSize,
            dictType: searchText,
            dictName: searchText
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
        setDrawerTitle('新增字典类型');
        setCurrentRecord(null);
        form.resetFields();
        setDrawerVisible(true);
    };

    // 处理编辑
    const handleEdit = (record: any) => {
        setOperationType('edit');
        setDrawerTitle('编辑字典类型');
        setCurrentRecord(record);
        form.setFieldsValue(record);
        setDrawerVisible(true);
    };

    // 处理查看
    const handleView = (record: any) => {
        setOperationType('view');
        setDrawerTitle('查看字典类型');
        setCurrentRecord(record);
        form.setFieldsValue(record);
        setDrawerVisible(true);
    };

    // 处理删除
    const handleDelete = (record: any) => {
        Modal.confirm({
            title: '确认删除',
            content: `确定要删除字典类型【${record.dictName}】吗？`,
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
                const addRes = await request({
                    url: url.add,
                    method: 'POST',
                    data: values
                });
                if (addRes.data) {
                    showMessage.success('新增成功');
                } else {
                    showMessage.warning('新增失败, 存在相同的字典类型');
                    return
                }
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

    const refreshCache = async () => {
        try {
            await request({
                url: url.refreshCache,
                method: 'GET'
            });
            showMessage.success('缓存刷新成功');
        } catch (error) {
            showMessage.error('缓存刷新失败');
        }
    };

    return (
        <div className="p-4">
            <div className="flex justify-between items-center mb-4">
                <h3 className="text-lg font-bold">字典管理</h3>
                <div className="flex gap-2">
                    <Button className='cream-button' type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
                        新增字典类型
                    </Button>
                    <Button className='cream-button' onClick={refreshCache} icon={<RedoOutlined />}>
                        刷新缓存
                    </Button>
                </div>
            </div>

            {/* 搜索区域 */}
            <div className="flex mb-4 gap-2">
                <Input
                    placeholder="请输入字典类型或名称"
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

            {/* 统一抽屉：新增/编辑/查看 */}
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
                        name="dictType"
                        label="字典类型"
                        rules={operationType !== 'view' ? [
                            { required: true, message: '请输入字典类型' },
                            { pattern: /^[a-zA-Z0-9_]+$/, message: '字典类型只能包含字母、数字和下划线' }
                        ] : []}
                    >
                        <Input
                            className='cream-input'
                            placeholder="请输入字典类型"
                            disabled={operationType === 'view'}
                        />
                    </Form.Item>

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
                        name="dictDesc"
                        label="字典描述"
                    >
                        <Input.TextArea
                            className='cream-input'
                            placeholder="请输入字典描述"
                            rows={4}
                            disabled={operationType === 'view'}
                        />
                    </Form.Item>
                </Form>
            </WtDrawer>

            {/* 字典项抽屉 */}
            <WtDrawer
                title={`字典项管理 - ${currentDictType.dictType}`}
                open={dictItemDrawerVisible}
                onClose={() => setDictItemDrawerVisible(false)}
                width={1000}
                okButtonProps={{ className: 'cream-button' }}
                cancelButtonProps={{ className: 'cream-button' }}
            >
                <DictItem
                    dictTypeId={currentDictType.id}
                    dictType={currentDictType.dictType}
                    onClose={() => setDictItemDrawerVisible(false)}
                />
            </WtDrawer>
        </div>
    );
}
