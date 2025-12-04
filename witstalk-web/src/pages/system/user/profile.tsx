import { useState, useEffect } from 'react';
import {
  Card,
  Avatar,
  Form,
  Input,
  Button,
  Upload,
  message,
  Divider,
  Space
} from 'antd';
import { UserOutlined, CameraOutlined, LockOutlined, MailOutlined } from '@ant-design/icons';
import { useUserStore } from '~/store/userStore';
import { request } from '~/util/request';
import { showMessage } from '~/util/msg';

const { Password } = Input;

const url = {
  updateAvatar: '/auth/updateAvatar',
  updateProfile: '/auth/updateProfile',
  updatePassword: '/auth/updatePassword',
};

export default function Profile() {
  const [basicForm] = Form.useForm();
  const [passwordForm] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const { userInfo, updateUserInfo } = useUserStore();
  const [avatarFile, setAvatarFile] = useState<File | null>(null);

  // 初始化表单数据
  useEffect(() => {
    if (userInfo) {
      basicForm.setFieldsValue({
        nickName: userInfo.nickName,
        email: userInfo.email,
      });
    }
  }, [userInfo, basicForm]);

  // 将图片转换为base64
  // const getBase64 = (file: File): Promise<string> => {
  //   return new Promise((resolve, reject) => {
  //     const reader = new FileReader();
  //     reader.readAsDataURL(file);
  //     reader.onload = () => resolve(reader.result as string);
  //     reader.onerror = error => reject(error);
  //   });
  // };

  // 处理头像上传
  const handleAvatarUpload = async (file: File) => {
    const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
    if (!isJpgOrPng) {
      message.error('只能上传 JPG 或 PNG 格式的图片!');
      return false;
    }
    const isLt2M = file.size / 1024 / 1024 < 2;
    if (!isLt2M) {
      message.error('图片大小不能超过 2MB!');
      return false;
    }
    setAvatarFile(file);
    return false;
  };

  // 保存头像
  const handleSaveAvatar = async () => {
    if (!avatarFile) {
      message.warning('请选择要上传的头像!');
      return;
    }

    setLoading(true);
    try {
      const formData = new FormData();
      formData.append('avatar', avatarFile);

      await request({
        url: url.updateAvatar,
        method: 'POST',
        data: formData,
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      showMessage.success('头像更新成功!');
      await updateUserInfo(); // 更新用户信息
      setAvatarFile(null);
    } catch {
      showMessage.error('头像更新失败!');
    } finally {
      setLoading(false);
    }
  };

  // 保存个人信息
  const handleSaveProfile = async (values: any) => {
    setLoading(true);
    try {
      await request({
        url: url.updateProfile,
        method: 'POST',
        data: values,
      });

      showMessage.success('个人信息更新成功!');
      await updateUserInfo(); // 更新用户信息
    } catch {
      showMessage.error('个人信息更新失败!');
    } finally {
      setLoading(false);
    }
  };

  // 保存密码
  const handleSavePassword = async (values: any) => {
    setLoading(true);
    try {
      await request({
        url: url.updatePassword,
        method: 'POST',
        data: values,
      });

      showMessage.success('密码更新成功!');
      passwordForm.resetFields();
    } catch {
      showMessage.error('密码更新失败!');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 p-6" style={{ background: 'linear-gradient(135deg, var(--cream-bg-1) 0%, var(--cream-bg-2) 100%)' }}>
      <div className="max-w-4xl mx-auto">
        <h1 className="text-2xl font-bold mb-6 cream-title">个人中心</h1>

        {/* 头像和基本信息 */}
        <Card className="mb-6 cream-card cream-fade-in">
          <div className="flex flex-col md:flex-row items-center md:items-start gap-6">
            {/* 头像区域 */}
            <div className="flex flex-col items-center gap-4">
              <Avatar
                size={128}
                src={userInfo.avatar ? `data:image/jpeg;base64,${userInfo.avatar}` : undefined}
                icon={<UserOutlined />}
              />
              <Space direction="vertical">
                <Upload
                  accept="image/*"
                  beforeUpload={handleAvatarUpload}
                  showUploadList={false}
                >
                  <Button icon={<CameraOutlined />} className="cream-button">
                    选择头像
                  </Button>
                </Upload>
                {avatarFile && (
                  <Button
                    type="primary"
                    className="cream-button"
                    onClick={handleSaveAvatar}
                    loading={loading}
                  >
                    保存头像
                  </Button>
                )}
              </Space>
            </div>

            {/* 基本信息 */}
            <div className="flex-1">
              <h2 className="text-xl font-semibold mb-4">基本信息</h2>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <p className="text-sm text-gray-500">用户名</p>
                  <p className="font-medium">{userInfo.username}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">昵称</p>
                  <p className="font-medium">{userInfo.nickName}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">邮箱</p>
                  <p className="font-medium">{userInfo.email}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">注册时间</p>
                  <p className="font-medium">{userInfo.createTime ? new Date(userInfo.createTime).toLocaleString() : '-'}</p>
                </div>
              </div>
            </div>
          </div>
        </Card>

        <Divider />

        {/* 编辑个人信息 */}
        <Card title="编辑个人信息" className="mb-6 cream-card cream-fade-in">
          <Form
            form={basicForm}
            layout="vertical"
            onFinish={handleSaveProfile}
            initialValues={{
              nickName: userInfo.nickName,
              email: userInfo.email,
            }}
          >
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <Form.Item
                name="nickName"
                label="昵称"
                rules={[{ required: true, message: '请输入昵称' }]}
              >
                <Input prefix={<UserOutlined />} placeholder="请输入昵称" className="cream-input" />
              </Form.Item>

              <Form.Item
                name="email"
                label="邮箱"
                rules={[
                  { required: true, message: '请输入邮箱' },
                  { type: 'email', message: '请输入有效的邮箱地址' }
                ]}
              >
                <Input prefix={<MailOutlined />} placeholder="请输入邮箱" className="cream-input" />
              </Form.Item>
            </div>

            <Form.Item>
              <Button type="primary" htmlType="submit" loading={loading} className="cream-button">
                保存个人信息
              </Button>
            </Form.Item>
          </Form>
        </Card>

        {/* 修改密码 */}
        <Card title="修改密码" className="mt-3 cream-card cream-fade-in" style={{marginTop: '20px'}}>
          <Form
            form={passwordForm}
            layout="vertical"
            onFinish={handleSavePassword}
          >
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <Form.Item
                name="oldPassword"
                label="旧密码"
                rules={[{ required: true, message: '请输入旧密码' }]}
              >
                <Password prefix={<LockOutlined />} placeholder="请输入旧密码" className="cream-input" />
              </Form.Item>

              <Form.Item
                name="newPassword"
                label="新密码"
                rules={[
                  { required: true, message: '请输入新密码' },
                  { min: 6, message: '密码长度不能少于6位' }
                ]}
              >
                <Password prefix={<LockOutlined />} placeholder="请输入新密码" className="cream-input" />
              </Form.Item>

              <Form.Item
                name="confirmPassword"
                label="确认新密码"
                dependencies={['newPassword']}
                rules={[
                  { required: true, message: '请确认新密码' },
                  ({ getFieldValue }) => ({
                    validator(_, value) {
                      if (!value || getFieldValue('newPassword') === value) {
                        return Promise.resolve();
                      }
                      return Promise.reject(new Error('两次输入的密码不一致!'));
                    },
                  }),
                ]}
              >
                <Password prefix={<LockOutlined />} placeholder="请确认新密码" className="cream-input" />
              </Form.Item>
            </div>

            <Form.Item>
              <Button type="primary" htmlType="submit" loading={loading} className="cream-button">
                修改密码
              </Button>
            </Form.Item>
          </Form>
        </Card>
      </div>
    </div>
  );
}
