import WitsTable from '~/components/witsTable';
import { Button } from 'antd';

const columns = [
    {
        title: 'Name',
        dataIndex: 'name',
        key: 'name',
        render: (text) => <a>{text}</a>,
    },
    {
        title: 'Age',
        dataIndex: 'age',
        key: 'age',
    },
    {
        title: 'Address',
        dataIndex: 'address',
        key: 'address',
    },
]
const toolbar = [
    <Button type="primary" onClick={() => { debugger}}>添加</Button>,
]

export default function Dict() {
    return (
        <div>
            <WitsTable columns={columns} url={{}} toolbar={toolbar}></WitsTable>
        </div>
    );
}