import { FloatButton } from 'antd';
import { useUserStore } from '~/store/userStore';
import type { FloatButtonProps } from 'antd';

/**
 * <AuthFloatButton permission="sys:user:add" icon={<PlusOutlined />} />
 */
interface AuthFloatButtonProps extends FloatButtonProps {
  permission?: string; // 所需权限
}

export default function AuthFloatButton({ permission, children, ...props }: AuthFloatButtonProps) {
  const { hasPermission, permissions } = useUserStore();

  // 如果没有指定权限，则直接显示悬浮按钮
  if (!permission) {
    return <FloatButton {...props}>{children}</FloatButton>;
  }

  // 检查用户是否是管理员（拥有admin权限或所有权限）
  const isAdmin = permissions.includes('admin') || permissions.includes('*');
  if (isAdmin) {
    return <FloatButton {...props}>{children}</FloatButton>;
  }

  // 检查用户是否拥有所需权限
  const isAllowed = hasPermission(permission);

  // 如果有权限则显示悬浮按钮，否则不渲染
  return isAllowed ? <FloatButton {...props}>{children}</FloatButton> : null;
}
