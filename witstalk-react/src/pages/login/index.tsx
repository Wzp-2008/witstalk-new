import React from 'react';
import { Button, Card, Form, Input, Typography, Row, Col, Checkbox } from 'antd';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router';
import { useLogin } from '~/hook/useAuth.ts';
import './login.css';
import {showMessage} from "~/util/msg";

const { Title, Paragraph } = Typography;
const { Item } = Form;

const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm<{username: string; password: string}>();
  const { login, isLoading } = useLogin();

  const onFinish = async (values: {username: string; password: string}) => {
    const res = await login(values.username, values.password);
    if (res.success) {
      showMessage.success('登录成功！');
      navigate('/');
    } else { 
      showMessage.error('登录失败，请检查用户名和密码');
    }
  };

  return (
    <div className="login-container">
      <Row justify="center" align="middle" className="login-row">
        <Col xs={22} sm={20} md={14} lg={10} xl={8}>
          <Card className="login-card" bordered={false}>
            <div className="login-header">
              <Title level={3} className="login-title">Witstalk</Title>
              <Paragraph className="login-subtitle">欢迎回来，请登录您的账户</Paragraph>
            </div>
            
            <Form
              form={form}
              layout="vertical"
              onFinish={onFinish}
              className="login-form"
            >
              <Item
                name="username"
                label="用户名"
                rules={[
                  { required: true, message: '请输入用户名' },
                  { 
                    pattern: /^[a-zA-Z0-9_]{3,20}$/, 
                    message: '用户名只能包含字母、数字和下划线，长度3-20位'
                  }
                ]}
              >
                <Input
                  prefix={<UserOutlined />}
                  placeholder="请输入用户名"
                  className="login-input"
                />
              </Item>
              
              <Item
                name="password"
                label="密码"
                rules={[
                  { required: true, message: '请输入密码' },
                  { 
                    pattern: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{6,20}$/, 
                    message: '密码至少6位，必须包含字母和数字'
                  }
                ]}
              >
                <Input.Password
                  prefix={<LockOutlined />}
                  placeholder="请输入密码"
                  className="login-input"
                />
              </Item>
              
              <Item name="remember" valuePropName="checked" className="login-remember">
                <Checkbox>记住我</Checkbox>
                <Button type="text" className="login-forgot">忘记密码？</Button>
              </Item>
              
              <Item className="login-button-item">
                <Button
                  type="primary"
                  htmlType="submit"
                  className="login-button"
                  loading={isLoading}
                  block
                >
                  登录
                </Button>
              </Item>
              
              <div className="login-register">
                <span>还没有账号？</span>
                <Button type="link" onClick={() => navigate('/register')}>立即注册</Button>
              </div>
            </Form>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default LoginPage;