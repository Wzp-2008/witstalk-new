import { Masonry, Card } from 'antd';
import { SettingOutlined } from '@ant-design/icons';
import logo from '~/assets/images/defaultAvatar.svg';
import { useNavigate } from 'react-router';
import './system.css';
import bgc from '~/assets/images/bgc.jpg';

export default function System() {
    const navigate = useNavigate();

    const systemItems = [
        {
            img: logo,
            title: '用户管理',
            onClick: () => {
                navigate('/system/user');
            }
        },
        {
            img: logo,
            title: '菜单管理',
            onClick: () => {
                navigate('/system/menu');
            }
        },
        {
            img: logo,
            title: '角色管理',
            onClick: () => {
                navigate('/system/role');
            }
        },
        {
            img: logo,
            title: '字典管理',
            onClick: () => {
                navigate('/system/dict');
            }
        }
    ]

    const items = []
    systemItems.forEach((item, index) => {
        items.push({
            key: index,
            children: (
                <Card
                    className="system-card card-background"
                    onClick={item.onClick}
                    cover={
                        <img
                            alt="icon"
                            className="system-card-icon"
                            src={ item.img }
                        />
                    }
                >
                    <Card.Meta 
                        title={<span className="system-card-title">{item.title}</span>} 
                        className="system-card-title"
                    />
                </Card>
            )
        })
    })
    
    return (
        <>
            <div className="system-container" style={{ background: `url(${bgc}) center/cover no-repeat` }}>
                <Masonry
                    columns={8}
                    gutter={16}
                    bordered={true}
                    items={items}
                >
                </Masonry>
            </div>
        </>
    )
}