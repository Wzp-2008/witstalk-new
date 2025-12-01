import React from 'react';
import { useUserStore } from '~/store/userStore';

/**
 * <AuthWrapper permission="sys:user:view">
 *  <div className="user-info">用户信息</div>
 * </AuthWrapper>
 */
interface AuthWrapperProps {
  permission?: string; // 所需权限
  children: React.ReactNode;
}

export default function AuthWrapper({ permission, children }: AuthWrapperProps) {
  const { hasPermission, permissions } = useUserStore();

  // 如果没有指定权限，则直接显示子组件
  if (!permission) {
    return <>{children}</>;
  }

  // 检查用户是否是管理员（拥有admin权限或所有权限）
  const isAdmin = permissions.includes('admin') || permissions.includes('*');
  if (isAdmin) {
    return <>{children}</>;
  }

  // 检查用户是否拥有所需权限
  const isAllowed = hasPermission(permission);

  // 如果有权限则渲染子元素，否则不渲染
  return isAllowed ? <>{children}</> : null;
}