import React from 'react';
import { Button } from 'antd';
import { useUserStore } from '~/store/userStore';
import type { ButtonProps } from 'antd';

/**
 * <AuthButton permission="sys:user:add">新增用户</AuthButton>
 */

interface AuthButtonProps extends ButtonProps {
  permission?: string; // 所需权限
  children: React.ReactNode;
}

export default function AuthButton({ permission, children, ...props }: AuthButtonProps) {
  const { hasPermission, permissions } = useUserStore();

  // 如果没有指定权限，则直接显示按钮
  if (!permission) {
    return <Button {...props}>{children}</Button>;
  }

  // 检查用户是否是管理员（拥有admin权限或所有权限）
  const isAdmin = permissions.includes('admin') || permissions.includes('*');
  if (isAdmin) {
    return <Button {...props}>{children}</Button>;
  }

  // 检查用户是否拥有所需权限
  const isAllowed = hasPermission(permission);

  // 如果有权限则显示按钮，否则不渲染
  return isAllowed ? <Button {...props}>{children}</Button> : null;
}
