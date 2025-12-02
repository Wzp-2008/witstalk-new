// src/utils/msg.tsx（修正版）
import { message } from 'antd';
import { createContext, useContext, useEffect } from 'react';
import type {ReactNode} from 'react'

// 创建上下文
const MessageContext = createContext<ReturnType<typeof message.useMessage>[0] | null>(null);

// 提供器组件
export const MessageProvider = ({ children }: { children: ReactNode }) => {
    const [ messageApi, contextHolder ] = message.useMessage();

    // 将实例存入全局变量，供非组件环境使用
    useEffect(() => {
        globalMessageApi = messageApi;
    }, [messageApi]);

    return (
        <MessageContext.Provider value={messageApi}>
            {contextHolder}
            {children}
        </MessageContext.Provider>
    );
};

// 自定义 hook，供组件内使用
export const useMessage = () => {
    const messageApi = useContext(MessageContext);
    if (!messageApi) {
        throw new Error('请在 MessageProvider 中使用 useMessage');
    }
    return messageApi;
};

// 全局变量存储实例（供非组件环境）
let globalMessageApi: ReturnType<typeof message.useMessage>[0] | null = null;

// 非组件环境调用的方法（移除了所有 Hook 调用）
export const showMessage = {
    error: (content: string) => {
        if (globalMessageApi) {
            globalMessageApi.error(content);
        } else {
            // 降级处理（初始化完成前可能短暂触发）
            console.warn('message 实例尚未初始化，使用默认 message');
            message.error(content);
        }
    },
    success: (content: string) => {
        if (globalMessageApi) {
            globalMessageApi.success(content);
        } else {
            console.warn('message 实例尚未初始化，使用默认 message');
            message.success(content);
        }
    },
    warning: (content: string) => {
        if (globalMessageApi) {
            globalMessageApi.warning(content);
        } else {
            console.warn('message 实例尚未初始化，使用默认 message');
            message.warning(content);
        }
    },
    info: (content: string) => {
        if (globalMessageApi) {
            globalMessageApi.info(content);
        } else {
            console.warn('message 实例尚未初始化，使用默认 message');
            message.info(content);
        }
    }
};
