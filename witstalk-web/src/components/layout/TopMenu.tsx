import {Menu} from 'antd';
import { HomeOutlined, MessageOutlined, SettingOutlined, RobotOutlined, FolderOutlined } from '@ant-design/icons';
import type { MenuProps } from 'antd';
import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router';

type MenuItem = Required<MenuProps>['items'][number];

const items: MenuItem[] = [
    {
        label: '首页',
        key: '/',
        icon: <HomeOutlined />,
    },
    {
        label: 'witstalk',
        key: '/witstalk',
        icon: <MessageOutlined />,
    },
    {
        label: '文件',
        key: '/file',
        icon: <FolderOutlined />
    },
    {
        label: '游戏',
        key: '/game',
        icon: <RobotOutlined />
    },
    {
        label: '系统',
        key: '/system',
        icon: <SettingOutlined />
    }
];

export default function TopMenu() {
    const navigate = useNavigate();
    const location = useLocation();
    const [current, setCurrent] = useState(location.pathname);

    const onClick = (event: any) => {
        navigate(event.key);
        setCurrent(event.key);
    }

    return (
        <>
            <Menu
                onClick={onClick}
                style={{ width: '50vw', backgroundColor: 'var(--cream-light)' }}
                selectedKeys={[current]}
                mode="horizontal"
                items={items}
            />
        </>
    )
}
