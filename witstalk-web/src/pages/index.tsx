import { Card, Typography} from 'antd';

const {Title, Paragraph} = Typography;

export default function AppIndex () {
    return (
        <div className="cream-bg" style={{minHeight: "86vh"}}>
            <Card className="cream-card cream-fade-in max-w-md mx-auto" style={{ borderRadius: '12px' }}>
                <div className="cream-header">
                    <Title level={3} className="cream-title">欢迎使用 Witstalk</Title>
                    <Paragraph className="cream-subtitle">这是一个智能聊天平台</Paragraph>
                </div>
            </Card>
        </div>
    )
}
