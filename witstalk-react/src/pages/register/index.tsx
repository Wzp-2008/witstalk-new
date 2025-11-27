import React from 'react';
import { Button, Card, Form, Input, Typography, Row, Col } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined, ProfileOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router';
import './register.css';
import { useRegister } from '~/hook/useAuth.ts';
import {showMessage} from "~/util/msg";

const { Title, Paragraph } = Typography;
const { Item } = Form;

const RegisterPage: React.FC = () => {
  const navigate = useNavigate();
  const [form] = Form.useForm<{username: string; password: string; confirmPassword: string; nickname: string; email: string}>();
  const { register, isLoading } = useRegister();

  const onFinish = async (values: {username: string; password: string; confirmPassword: string; nickname: string; email: string}) => {
    const { username, password, nickname, email } = values;
    const res = await register(username, password, nickname, email);
    if (res.success) {
      showMessage.success('注册成功，请登录');
      navigate('/login');
    } else { 
      showMessage.error('注册失败，请检查输入信息');
    }
  };

  return (
    <div className="register-container">
      <Row justify="center" align="middle" className="register-row">
        <Col xs={22} sm={20} md={14} lg={10} xl={8}>
          <Card className="register-card" bordered={false}>
            <div className="register-header">
              <Title level={3} className="register-title">Witstalk</Title>
              <Paragraph className="register-subtitle">创建新账户，开始使用</Paragraph>
            </div>
            
            <Form
              form={form}
              layout="vertical"
              onFinish={onFinish}
              className="register-form"
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
                  className="register-input"
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
                  className="register-input"
                />
              </Item>
              
              <Item
                name="confirmPassword"
                label="确认密码"
                dependencies={['password']}
                rules={[
                  { required: true, message: '请确认密码' },
                  ({ getFieldValue }) => ({
                    validator(_, value) {
                      if (!value || getFieldValue('password') === value) {
                        return Promise.resolve();
                      }
                      return Promise.reject(new Error('两次输入的密码不一致'));
                    },
                  }),
                  {
                    pattern: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{6,20}$/,
                    message: '密码至少6位，必须包含字母和数字'
                  }
                ]}
              >
                <Input.Password
                  prefix={<LockOutlined />}
                  placeholder="请再次输入密码"
                  className="register-input"
                />
              </Item>
              
              <Item
                name="nickname"
                label="昵称"
                rules={[
                  { required: true, message: '请输入昵称' },
                  { 
                    pattern: /^[\u4e00-\u9fa5a-zA-Z0-9_]{2,20}$/, 
                    message: '昵称只能包含中文、字母、数字和下划线，长度2-20位'
                  }
                ]}
              >
                <Input
                  prefix={<ProfileOutlined />}
                  placeholder="请输入昵称"
                  className="register-input"
                />
              </Item>
              
              <Item
                name="email"
                label="邮箱"
                rules={[
                  { required: true, message: '请输入邮箱' },
                  { 
                    pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/, 
                    message: '请输入有效的邮箱地址'
                  }
                ]}
              >
                <Input
                  prefix={<MailOutlined />}
                  placeholder="请输入邮箱"
                  className="register-input"
                />
              </Item>
              
              <Item className="register-button-item">
                <Button
                  type="primary"
                    htmlType="submit"
                    className="register-button"
                    loading={isLoading}
                    block
                  >
                  注册
                </Button>
              </Item>
              
              <div className="register-login">
                <span>已有账号？</span>
                <Button type="link" onClick={() => navigate('/login')}>立即登录</Button>
              </div>
            </Form>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default RegisterPage;