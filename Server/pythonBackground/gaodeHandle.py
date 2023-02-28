import joblib
import tsfresh as tsf
import numpy as np
import pandas as pd
import copy

# 高德模型
loaded_model = joblib.load('model_gaode/clf_gaode_m_4.m')
count = 8


def get_abs_energy(x):
    return tsf.feature_extraction.feature_calculators.abs_energy(x)


def get_absolute_sum_of_changes(x):
    return tsf.feature_extraction.feature_calculators.absolute_sum_of_changes(x)


def get_count_above_mean(x):
    return tsf.feature_extraction.feature_calculators.count_above_mean(x)


def get_count_below_mean(x):
    return tsf.feature_extraction.feature_calculators.count_below_mean(x)


def get_fft_aggregated(x):
    param = [{'aggtype': 'centroid'}]
    return list(tsf.feature_extraction.feature_calculators.fft_aggregated(x, param))[0][1]


def get_first_location_of_minimum(x):
    return tsf.feature_extraction.feature_calculators.first_location_of_minimum(x)


def get_first_location_of_maximum(x):
    return tsf.feature_extraction.feature_calculators.first_location_of_maximum(x)


def get_len(x):
    return np.count_nonzero(x > 0)


def get_slope(x):
    slope, intercept = np.polynomial.polynomial.Polynomial.fit(np.arange(1, len(x) + 1), np.array(x), deg=1)
    return slope


# 处理处理初次分割的时间序列
def handleGaodeRTList(list):
    # 1. 根据 rendertime == -1 则说明这是某一段的起点要进行分段
    id = 0
    # 先找到第一个rt['renderTime'] == -1 的元素的下标，从该元素开始寻找,之前的元素全部删除
    for index, rt in enumerate(list):
        if rt['renderTime'] == -1:
            id = index
            break;
    list = list[id:]
    ans = []
    res = segmentation(list)
    for i in res:
        # 2. 预处理 ：截长补短
        data = preprocess(i, count)
        print("预处理后", data)
        # 3. 添加特征
        data = data.T
        X = data
        # 计算序列大于0的数量
        l = X.apply(get_len, axis=1)
        a = X.apply(get_count_above_mean, axis=1)
        b = X.apply(get_count_below_mean, axis=1)
        slope = X.apply(get_slope, axis=1)

        # 最大值
        data['max'] = X.max(axis=1)
        # 最小值
        data['min'] = X.min(axis=1)
        # 平均值
        data['mean'] = X.mean(axis=1)
        # 标准差
        data['std'] = X.std(axis=1)
        # 偏度 skew
        data['skew'] = X.skew(axis=1)
        # 峰度 kurtosis
        data['kurtosis'] = X.kurtosis(axis=1)
        data['slope'] = slope
        data["len"] = l
        data["count_above_mean"] = a
        data["count_below_mean"] = b
        data = np.array(data)
        print("最终预测的数据:", data)
        r = loaded_model.predict(data)
        ans.append(r)
        # 4. 预测
        print("预测结果：{}".format(r))  # 输出分类结果标签
        print("分类概率：{}".format(loaded_model.predict_proba(data)))
    return ans
    pass


# 分割
def segmentation(list):
    # 1. 遍历list， 如果遇到 rendertime == -1 则说明这是某一段的结尾要进行分割， 并把序列存到res[]中 大list分割成小list
    res = []
    arr = []

    for rt in list:
        if rt['renderTime'] != -1:
            arr.append(rt)
        else:
            if len(arr) != 0:
                # 深拷贝得到与原来无关的新列表
                res.append(copy.deepcopy(arr))
            arr.clear()
    if len(arr) > 0:
        res.append(copy.deepcopy(arr))
        arr.clear()

    print("分割组数：", len(res))
    return res
    pass


# 预处理 ：截长补短（补0）
def preprocess(list, count):
    df = pd.DataFrame(list)
    df = pd.DataFrame(df['renderTime'])
    # 截长补短
    if df.shape[0] > count:
        df = df.iloc[:count]
    elif df.shape[0] < count:
        for i in range(count - df.shape[0]):
            df = df.append({'renderTime': 0}, ignore_index=True)
    return pd.DataFrame(df)
