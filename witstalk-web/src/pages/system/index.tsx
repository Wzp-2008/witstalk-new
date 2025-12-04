import { Masonry, Card } from 'antd';
import logo from '~/assets/images/defaultAvatar.svg';
import { useNavigate } from 'react-router';
import './system.css';
import bgc from '~/assets/images/bgc.jpg';
import type {MasonryItemType} from "antd/es/masonry/MasonryItem";
import React from "react";

interface SystemItem {
    img: object;
    title: string;
    url: string;
}

export default function System() {
    const navigate = useNavigate();

    const systemItems: SystemItem[] = [
        {
            img: logo,
            title: '用户管理',
            url: '/system/user'
        },
        {
            img: logo,
            title: '菜单管理',
            url: '/system/menu'
        },
        {
            img: logo,
            title: '角色管理',
            url: '/system/role'
        },
        {
            img: logo,
            title: '字典管理',
            url: '/system/dict'
        },
        {
            img: logo,
            title: '频道管理',
            url: '/system/channel'
        }
    ]

    const items: MasonryItemType[] = systemItems.map((item, index) => ({
        key: index.toString(),
        children: (
            <Card
                className="system-card card-background"
                onClick={() => navigate(item.url)}
                cover={
                    <img
                        alt="icon"
                        className="system-card-icon"
                        src={ item.img as string }
                    />
                }
            >
                <Card.Meta
                    title={<span className="system-card-title">{item.title}</span>}
                    className="system-card-title"
                />
            </Card>
        )
    }))

    return (
        <>
            <div className="system-container" style={{ background: `url(${bgc}) center/cover no-repeat` }}>
                <Masonry
                    columns={8}
                    gutter={16}
                    items={items}
                >
                </Masonry>
            </div>
        </>
    );
}