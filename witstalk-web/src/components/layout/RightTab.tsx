import { Avatar, Button, Dropdown, Space, theme } from 'antd';
import { useUserStore } from '~/store/userStore';
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router';
import defaultAvatar from '~/assets/images/defaultAvatar.svg';

const { useToken } = theme;

export default function RightTab() {
    const { userInfo, restartUserInfo } = useUserStore();
    const [avatar, setAvatar] = useState<string>();
    const { token } = useToken();
    const navigate = useNavigate();

    useEffect(() => {
        if (!userInfo.avatar) {
            setAvatar(defaultAvatar);
            return;
        }
        setAvatar('data:image/png;base64,' + userInfo.avatar);
    }, [userInfo.avatar]);

    const contentStyle: React.CSSProperties = {
        backgroundColor: token.colorBgElevated,
        borderRadius: token.borderRadiusLG,
        boxShadow: token.boxShadowSecondary,
    };

    const logout = () => {
        restartUserInfo();
        window.localStorage.clear();
        window.sessionStorage.clear();
        navigate('/login');
    }

    return (
        <div>
            <Dropdown placement="bottom" arrow={{ pointAtCenter: true }} popupRender={
                () => (
                    <div style={contentStyle}>
                        <div style={{
                            padding: 8,
                            display: 'flex',
                            flexDirection: 'column',
                        }}>
                            <Button type="text">个人中心</Button>
                            <Button type="text" style={{ color: 'var(--cream-primary)'}} onClick={logout}>退出登录</Button>
                        </div>
                    </div>
                )
            }>
                <Space>
                    <Avatar size={48} src={avatar} />
                    <span className="cream-subtitle" style={{ fontSize: '1.3rem'}}>{userInfo.nickName}</span>
                </Space>
            </Dropdown>
        </div>
    )
}
