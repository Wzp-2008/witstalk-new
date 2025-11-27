import { useState } from 'react';
import { message } from 'antd';
import { request } from '~/util/request';

interface LoginResponse {
  success: boolean;
  message?: string;
  token?: string;
  user?: {
    id: string;
    username: string;
    nickname: string;
    email: string;
    role?: string;
  };
}

interface RegisterResponse {
  success: boolean;
  message?: string;
  userId?: string;
}

interface UseLoginReturn {
  login: (username: string, password: string, rememberMe?: boolean) => Promise<LoginResponse>;
  isLoading: boolean;
  error: string | null;
}

interface UseRegisterReturn {
  register: (username: string, password: string, nickname: string, email: string) => Promise<RegisterResponse>;
  isLoading: boolean;
  error: string | null;
}

// 登录Hook
export const useLogin = (): UseLoginReturn => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const login = async (username: string, password: string, rememberMe: boolean = false): Promise<LoginResponse> => {
    setIsLoading(true);
    setError(null);

    try {
      const res = await request({
        url: '/auth/login',
        method: 'POST',
        data: {
          username,
          password
        }
      })
      return {
        success: true,
        message: '登录成功',
        data: res.data
      };
    } catch (err: any) {
      const errorMessage = err.message || '登录失败，请检查用户名和密码';
      setError(errorMessage);
      
      return {
        success: false,
        message: errorMessage
      };
    } finally {
      setIsLoading(false);
    }
  };

  return { login, isLoading, error };
};

// 注册Hook
export const useRegister = (): UseRegisterReturn => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const register = async (username: string, password: string, nickname: string, email: string): Promise<RegisterResponse> => {
    setIsLoading(true);
    setError(null);

    try {
      const res = await request({
        url: '/auth/register',
        method: 'POST',
        data: {
          username,
          nickName: nickname,
          password,
          email
        }
      })
      return {
        success: true,
        message: '注册成功',
        data: res.data
      };
    } catch (err: any) {
      const errorMessage = err.message || '注册失败，请稍后重试';
      setError(errorMessage);
      
      return {
        success: false,
        message: errorMessage
      };
    } finally {
      setIsLoading(false);
    }
  };

  return { register, isLoading, error };
};

// 退出登录Hook
export const useLogout = () => {
  const logout = async (): Promise<void> => {
    try {
      // 模拟API调用，实际项目中这里应该调用真实的退出接口
      // await request.post('/api/auth/logout');

      // 清除本地存储的token和用户信息
      localStorage.removeItem('token');
      sessionStorage.removeItem('token');
      localStorage.removeItem('userInfo');
      sessionStorage.removeItem('userInfo');

      // 清除请求工具中的token
      if (request.clearToken) {
        request.clearToken();
      }

      message.success('退出登录成功');
    } catch (err) {
      message.error('退出登录失败');
    }
  };

  return { logout };
};

// 获取当前用户信息Hook
export const useCurrentUser = () => {
  const getUserInfo = (): LoginResponse['user'] | null => {
    const userInfoStr = localStorage.getItem('userInfo') || sessionStorage.getItem('userInfo');
    if (userInfoStr) {
      try {
        return JSON.parse(userInfoStr);
      } catch (e) {
        return null;
      }
    }
    return null;
  };

  const isAuthenticated = (): boolean => {
    const token = localStorage.getItem('token') || sessionStorage.getItem('token');
    return !!token;
  };

  return { getUserInfo, isAuthenticated };
};