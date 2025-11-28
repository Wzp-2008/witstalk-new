import type { TableProps } from 'antd';
import { Table, Button, Tooltip } from 'antd';
import { useEffect, useState } from 'react';
import useOnce from '~/hook/useOnce';
import { EditOutlined, EyeOutlined, DeleteOutlined } from '@ant-design/icons';

interface WitsTableColumnsProps extends TableProps {
    queryEnable: boolean
}

interface WitsTableProps {
    columns: WitsTableColumnsProps[];
    url: Record<string, string>;
    toolbar: ReactNode[];
}

export default function WitsTable(props: WitsTableProps) {
    const [data, setData] = useState([
        { name: '张三', age: 18, address: '北京' },
        { name: '李四', age: 20, address: '上海' },
        { name: '王五', age: 22, address: '广州' },
    ]);
    const [columns, setColumns] = useState(props.columns);
    
    useEffect(() => {
        // 检查是否已经添加了 key 列
        if (columns.find((item) => item.dataIndex === 'key')) {
            return;
        }
        setColumns([
            {
                title: '#',
                dataIndex: 'key',
                render: (text, record, index) => index + 1,
            },
            ...columns,
            {
                title: '操作',
                fixed: 'end',
                width: 150,
                render: () => {
                    return (
                        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                            <Tooltip placement="top" title="编辑">
                                <Button shape="circle" icon={<EditOutlined />} />
                            </Tooltip>
                            <Tooltip placement="top" title="详情">
                                <Button shape="circle" icon={<EyeOutlined />} />
                            </Tooltip>
                            <Tooltip placement="top" title="删除">
                                <Button shape="circle" icon={<DeleteOutlined />} />
                            </Tooltip>
                        </div>
                    );
                },
            }
        ])
    }, []);

    return (
        <div>
            <div style={{ display: 'flex' }}>
                {props.toolbar}
            </div>
            <Table bordered columns={columns} dataSource={data} />
        </div>
    );
}