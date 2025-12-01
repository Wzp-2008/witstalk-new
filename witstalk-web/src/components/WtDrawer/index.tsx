import React from 'react';
import { Drawer, Button, Space } from 'antd';

interface WtDrawerProps {
  title: string;
  open: boolean;
  onClose: () => void;
  onOk?: () => void;
  width?: number;
  placement?: 'left' | 'right' | 'top' | 'bottom';
  okText?: string;
  cancelText?: string;
  okButtonProps?: any;
  cancelButtonProps?: any;
  children: React.ReactNode;
}

export default function WtDrawer({
  title,
  open,
  onClose,
  onOk,
  width = 500,
  placement = 'right',
  okText = '确定',
  cancelText = '取消',
  okButtonProps = {},
  cancelButtonProps = {},
  children
}: WtDrawerProps) {
  return (
    <Drawer
      title={title}
      placement={placement}
      onClose={onClose}
      open={open}
      width={width}
      footer={
        <Space className="flex justify-end gap-2 w-full">
          <Button onClick={onClose} {...cancelButtonProps}>
            {cancelText}
          </Button>
          {onOk && (
            <Button type="primary" onClick={onOk} {...okButtonProps}>
              {okText}
            </Button>
          )}
        </Space>
      }
    >
      {children}
    </Drawer>
  );
}