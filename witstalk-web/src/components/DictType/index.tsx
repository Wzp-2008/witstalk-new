import { Select } from 'antd';
import { requestSWR } from '~/util/request';

const { Option } = Select;

const url = {
    list: '/system/sysDictType/listByDictType',
}

interface DictTypeProps {
    dictType: string;
    value?: any;
    onChange?: (value: any) => void;
    placeholder?: string;
    mode?: 'single' | 'multiple';
    maxTagCount?: number;
    maxSelectCount?: number;
    disabled?: boolean;
    className?: string;
}

export default function DictType({
    dictType,
    value,
    onChange,
    placeholder = '请选择',
    mode = 'single',
    maxTagCount = 3,
    maxSelectCount = 0,
    disabled = false,
    className = ''
}: DictTypeProps) {
    // 获取字典项数据
    const { data, error } = requestSWR({
        url: `${url.list}?dictType=${dictType}`,
        method: 'POST'
    });

    // 处理选择变化
    const handleChange = (value: any) => {
        if (onChange) {
            onChange(value);
        }
    };

    // 处理选择前的验证
    const handleBeforeSelect = (value: any) => {
        if (mode === 'multiple' && maxSelectCount > 0) {
            const currentValue = Array.isArray(value) ? value : [];
            if (currentValue.length >= maxSelectCount) {
                return false;
            }
        }
        return true;
    };

    return (
        <Select
            value={value}
            onChange={handleChange}
            placeholder={placeholder}
            mode={mode === 'multiple' ? 'multiple' : undefined}
            maxTagCount={maxTagCount}
            disabled={disabled}
            className={className}
            beforeSelect={handleBeforeSelect}
            loading={!data && !error}
        >
            {data?.map((item: any) => (
                <Option key={item.dictValue} value={item.dictValue}>
                    {item.dictName}
                </Option>
            ))}
        </Select>
    );
}
