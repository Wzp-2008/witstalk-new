import { useNavigate } from 'react-router';
import { Button, Card, Typography, Divider } from 'antd';
import { ReloadOutlined, HomeOutlined, InfoCircleOutlined } from '@ant-design/icons';
import { useState } from 'react';

const { Title, Paragraph, Text } = Typography;

// 定义错误边界组件的属性类型
interface ErrorBoundaryProps {
  error: unknown;
}

export function ErrorBoundary({ error }: ErrorBoundaryProps) {
  const navigate = useNavigate();
  const [showDetails, setShowDetails] = useState<boolean>(false);
  let stack: string | undefined = undefined;

  let message = '出错了！';
  let errorType = '未知错误';
  let details = '';
  
  // 错误处理逻辑
  if (typeof error === 'object' && error !== null && 'status' in error) {
    // 路由错误处理
    const routeError = error as { status: number; statusText?: string };
    errorType = '路由错误';
    message = routeError.status === 404 ? '404 - 页面未找到' : `${routeError.status} - 服务器错误`;
    details = routeError.status === 404 
      ? '抱歉，您请求的页面不存在或已被删除。' 
      : routeError.statusText || '服务器暂时无法处理您的请求。';
  } else if (error instanceof Error) {
    // JavaScript错误处理
    errorType = '应用错误';
    message = '应用发生错误';
    details = process.env.NODE_ENV === 'development' ? error.message : '应用在运行时出现了问题。';
    stack = error.stack;
  } else if (typeof error === 'string') {
    // 字符串错误处理
    errorType = '文本错误';
    message = '发生了错误';
    details = process.env.NODE_ENV === 'development' ? error : '系统遇到了未知问题。';
  }

  // 重试功能
  const handleRetry = (): void => {
    window.location.reload();
  };

  // 返回首页
  const handleGoHome = (): void => {
    navigate('/');
  };

  // 切换详情显示
  const toggleDetails = (): void => {
    setShowDetails(!showDetails);
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-[#fff8e8] to-[#f9f0d9] p-4">
      <Card className="w-full max-w-md bg-white/80 backdrop-blur-sm border-none shadow-lg rounded-xl">
        <div className="text-center">
          {/* 错误图标 */}
          <InfoCircleOutlined className="text-red-500 text-5xl mb-4" />
          
          {/* 错误标题 */}
          <Title level={3} className="text-gray-800 mb-2">
            {message}
          </Title>
          
          {/* 错误描述 */}
          <Paragraph className="text-gray-600 mb-4">
            {details}
          </Paragraph>
          
          <Divider className="my-4" />
          
          {/* 操作按钮 */}
          <div className="flex flex-col sm:flex-row gap-2 justify-center">
            <Button 
              type="primary" 
              icon={<ReloadOutlined />}
              onClick={handleRetry}
              className="bg-[#f97316] hover:bg-[#ea580c] border-none text-white"
            >
              重试
            </Button>
            <Button 
              icon={<HomeOutlined />}
              onClick={handleGoHome}
              className="bg-white border border-gray-300 text-gray-700 hover:bg-gray-50"
            >
              返回首页
            </Button>
          </div>
          
          {/* 开发环境下的错误详情 */}
          {process.env.NODE_ENV === 'development' && (stack || errorType !== '未知错误') && (
            <div className="mt-6">
              <Button 
                type="link" 
                onClick={toggleDetails}
                className="text-blue-500"
              >
                {showDetails ? '隐藏错误详情' : '显示错误详情'}
              </Button>
              
              {showDetails && (
                <div className="mt-4 p-3 bg-gray-100 rounded-lg text-left">
                  <div className="mb-2">
                    <Text strong>错误类型：</Text>
                    <Text>{errorType}</Text>
                  </div>
                  {stack && (
                    <div className="mt-2">
                      <Text strong>错误堆栈：</Text>
                      <pre className="mt-1 p-2 bg-gray-800 text-gray-100 text-xs overflow-auto max-h-40 rounded">
                        {stack}
                      </pre>
                    </div>
                  )}
                </div>
              )}
            </div>
          )}
        </div>
      </Card>
    </div>
  );
}
