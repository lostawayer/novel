<template>
    <div class="center-container">
        <div class="center-box">
            <el-tabs v-model="activeTab">
                <el-tab-pane label="个人信息" name="info">
                    <el-form :model="userInfo" label-width="100px">
                        <el-form-item label="用户名">
                            <el-input v-model="userInfo.yonghuming" disabled />
                        </el-form-item>
                        <el-form-item label="姓名">
                            <el-input v-model="userInfo.xingming" />
                        </el-form-item>
                        <el-form-item label="性别">
                            <el-select v-model="userInfo.xingbie">
                                <el-option label="男" value="男" />
                                <el-option label="女" value="女" />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="头像">
                            <FileUpload
                                :fileUrls="userInfo.touxiang"
                                :limit="1"
                                :multiple="false"
                                tip="点击上传头像（仅限1张）"
                                @change="(val: string) => (userInfo.touxiang = val)"
                            />
                        </el-form-item>
                        <el-form-item label="邮箱">
                            <el-input v-model="userInfo.youxiang" />
                        </el-form-item>
                        <el-form-item label="手机">
                            <el-input v-model="userInfo.shouji" />
                        </el-form-item>
                        <el-form-item label="会员状态">
                            <el-tag
                                :type="
                                    userInfo.vip === '是' ? 'warning' : 'info'
                                "
                                size="large"
                            >
                                {{
                                    userInfo.vip === "是"
                                        ? "VIP会员"
                                        : "普通用户"
                                }}
                            </el-tag>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="handleUpdate"
                                >保存修改</el-button
                            >
                        </el-form-item>
                    </el-form>
                </el-tab-pane>

                <el-tab-pane label="VIP会员" name="vip">
                    <div class="vip-container">
                        <div class="vip-status">
                            <el-icon
                                :size="60"
                                :color="
                                    userInfo.vip === '是'
                                        ? '#e6a23c'
                                        : '#909399'
                                "
                            >
                                <Trophy />
                            </el-icon>
                            <h2>
                                {{
                                    userInfo.vip === "是"
                                        ? "您已是VIP会员"
                                        : "您还不是VIP会员"
                                }}
                            </h2>
                            <p v-if="userInfo.vip === '是'" class="vip-tip">
                                尊享VIP特权，畅读所有VIP章节
                            </p>
                            <p v-else class="vip-tip">
                                开通VIP会员，解锁全部VIP章节内容
                            </p>
                        </div>

                        <div v-if="userInfo.vip !== '是'" class="vip-plans">
                            <h3>选择会员套餐</h3>
                            <div class="plan-list">
                                <div
                                    v-for="plan in vipPlans"
                                    :key="plan.type"
                                    :class="[
                                        'plan-item',
                                        { active: selectedPlan === plan.type },
                                    ]"
                                    @click="selectedPlan = plan.type"
                                >
                                    <div class="plan-name">{{ plan.name }}</div>
                                    <div class="plan-price">
                                        <span class="price"
                                            >¥{{ plan.price }}</span
                                        >
                                        <span
                                            class="original"
                                            v-if="plan.original"
                                            >¥{{ plan.original }}</span
                                        >
                                    </div>
                                    <div class="plan-desc">{{ plan.desc }}</div>
                                    <el-tag
                                        v-if="plan.tag"
                                        type="danger"
                                        size="small"
                                        class="plan-tag"
                                        >{{ plan.tag }}</el-tag
                                    >
                                </div>
                            </div>

                            <div class="pay-section">
                                <el-button
                                    type="warning"
                                    size="large"
                                    @click="handleBuyVip"
                                    :loading="buying"
                                >
                                    立即开通VIP
                                </el-button>
                                <p class="pay-tip">
                                    * 支付将跳转到支付宝沙箱环境完成
                                </p>
                            </div>
                        </div>

                        <div v-else class="vip-benefits">
                            <h3>VIP会员特权</h3>
                            <div class="benefit-list">
                                <div class="benefit-item">
                                    <el-icon :size="24" color="#e6a23c"
                                        ><Reading
                                    /></el-icon>
                                    <span>畅读VIP章节</span>
                                </div>
                                <div class="benefit-item">
                                    <el-icon :size="24" color="#e6a23c"
                                        ><Star
                                    /></el-icon>
                                    <span>专属VIP标识</span>
                                </div>
                                <div class="benefit-item">
                                    <el-icon :size="24" color="#e6a23c"
                                        ><Present
                                    /></el-icon>
                                    <span>更多特权开发中...</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </el-tab-pane>

                <el-tab-pane label="修改密码" name="password">
                    <el-form :model="passwordForm" label-width="100px">
                        <el-form-item label="原密码">
                            <el-input
                                v-model="passwordForm.oldPassword"
                                type="password"
                            />
                        </el-form-item>
                        <el-form-item label="新密码">
                            <el-input
                                v-model="passwordForm.newPassword"
                                type="password"
                            />
                        </el-form-item>
                        <el-form-item label="确认密码">
                            <el-input
                                v-model="passwordForm.confirmPassword"
                                type="password"
                            />
                        </el-form-item>
                        <el-form-item>
                            <el-button
                                type="primary"
                                @click="handleChangePassword"
                            >
                                修改密码
                            </el-button>
                        </el-form-item>
                    </el-form>
                </el-tab-pane>
            </el-tabs>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { useRoute } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { Trophy, Reading, Star, Present } from "@element-plus/icons-vue";
import { get, post } from "@/utils/request";
import { getUserInfo, setUserInfo } from "@/common/storage";
import FileUpload from "@/components/FileUpload.vue";
import { useUserStore } from "@/store";

const userStore = useUserStore();
const route = useRoute();
const activeTab = ref("info");
const buying = ref(false);
const selectedPlan = ref("month");

const userInfo = reactive<any>({
    id: "",
    yonghuming: "",
    xingming: "",
    xingbie: "",
    touxiang: "",
    youxiang: "",
    shouji: "",
    vip: "否",
});

const passwordForm = reactive({
    oldPassword: "",
    newPassword: "",
    confirmPassword: "",
});

const vipPlans = [
    {
        type: "month",
        name: "月度会员",
        price: 15,
        original: 30,
        desc: "30天VIP权益",
        tag: "限时5折",
    },
    {
        type: "quarter",
        name: "季度会员",
        price: 40,
        original: 90,
        desc: "90天VIP权益",
        tag: "超值",
    },
    {
        type: "year",
        name: "年度会员",
        price: 128,
        original: 360,
        desc: "365天VIP权益",
        tag: "最划算",
    },
];

// 获取用户信息
const loadUserInfo = async () => {
    const info = getUserInfo();
    if (info) {
        Object.assign(userInfo, info);
        // 从服务器获取最新VIP状态
        try {
            const res = await get("/yonghu/info", { userId: info.id });
            if (res.code === 0 && res.data) {
                userInfo.vip = res.data.vip || "否";
            }
        } catch (e) {
            console.error("获取VIP状态失败", e);
        }
    }
};

// 更新个人信息
const handleUpdate = async () => {
    console.log("=== 开始更新 ===");
    console.log("userStore.role:", userStore.role);
    console.log("userInfo:", JSON.stringify(userInfo));

    if (!userStore.role) {
        ElMessage.error("请先登录");
        return;
    }

    try {
        const res = await post(`/${userStore.role}/update`, userInfo);
        console.log("更新响应:", res);
        if (res.code === 0) {
            setUserInfo(userInfo);
            userStore.setUserInfo(userInfo);
            ElMessage.success("更新成功");
        } else {
            ElMessage.error(res.msg || "更新失败");
        }
    } catch (error) {
        console.error("更新失败:", error);
        ElMessage.error("更新失败");
    }
};

// 修改密码
const handleChangePassword = async () => {
    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
        ElMessage.error("两次密码不一致");
        return;
    }

    try {
        const res = await post(`/${userStore.role}/update`, {
            id: userInfo.id,
            mima: passwordForm.newPassword,
        });
        if (res.code === 0) {
            ElMessage.success("密码修改成功，请重新登录");
        }
    } catch (error) {
        console.error("修改密码失败:", error);
    }
};

// 购买VIP
const handleBuyVip = async () => {
    const plan = vipPlans.find((p) => p.type === selectedPlan.value);

    try {
        await ElMessageBox.confirm(
            `确认开通${plan?.name}？价格：¥${plan?.price}`,
            "确认购买",
            {
                confirmButtonText: "去支付",
                cancelButtonText: "取消",
                type: "warning",
            }
        );

        buying.value = true;

        // 调用后端创建支付宝订单
        const res = await post("/alipay/create", {
            userId: userInfo.id,
            vipType: selectedPlan.value,
        });

        if (res.code === 0 && res.data?.payForm) {
            // 创建临时div来提交支付宝表单
            const div = document.createElement("div");
            div.innerHTML = res.data.payForm;
            document.body.appendChild(div);

            // 自动提交表单，跳转到支付宝支付页面
            const form = div.querySelector("form");
            if (form) {
                form.submit();
            }
        } else {
            ElMessage.error(res.msg || "创建订单失败");
        }
    } catch (e: any) {
        if (e !== "cancel") {
            console.error("支付失败:", e);
            ElMessage.error("支付失败");
        }
    } finally {
        buying.value = false;
    }
};

onMounted(() => {
    loadUserInfo();

    // 处理支付宝回调
    const payResult = route.query.payResult;
    const tab = route.query.tab;

    if (tab === "vip") {
        activeTab.value = "vip";
    }

    if (payResult === "success") {
        // 支付成功，调用后端确认并刷新VIP状态
        const outTradeNo = route.query.out_trade_no as string;
        if (outTradeNo) {
            // 调用后端确认支付
            get('/alipay/return', { out_trade_no: outTradeNo }).then(() => {
                loadUserInfo().then(() => {
                    if (userInfo.vip === "是") {
                        ElMessage.success("🎉 恭喜您成为VIP会员！");
                    }
                });
            });
        } else {
            // 没有订单号，直接刷新状态
            setTimeout(async () => {
                await loadUserInfo();
                if (userInfo.vip === "是") {
                    ElMessage.success("🎉 恭喜您成为VIP会员！");
                }
            }, 1000);
        }
    }
});
</script>

<style lang="scss" scoped>
.center-container {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
}

.center-box {
    background: #fff;
    padding: 30px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

:deep(.el-select) {
    width: 100%;
}

.vip-container {
    padding: 20px;
}

.vip-status {
    text-align: center;
    padding: 30px 0;
    border-bottom: 1px solid #eee;

    h2 {
        margin: 15px 0 10px;
        color: #333;
    }

    .vip-tip {
        color: #999;
        font-size: 14px;
    }
}

.vip-plans {
    padding: 30px 0;

    h3 {
        text-align: center;
        margin-bottom: 25px;
        color: #333;
    }
}

.plan-list {
    display: flex;
    justify-content: center;
    gap: 20px;
    flex-wrap: wrap;
}

.plan-item {
    position: relative;
    width: 180px;
    padding: 25px 20px;
    border: 2px solid #e4e7ed;
    border-radius: 12px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
        border-color: #e6a23c;
        transform: translateY(-3px);
    }

    &.active {
        border-color: #e6a23c;
        background: linear-gradient(135deg, #fff9e6 0%, #fff 100%);
        box-shadow: 0 4px 12px rgba(230, 162, 60, 0.3);
    }

    .plan-name {
        font-size: 16px;
        font-weight: bold;
        color: #333;
        margin-bottom: 15px;
    }

    .plan-price {
        margin-bottom: 10px;

        .price {
            font-size: 28px;
            font-weight: bold;
            color: #e6a23c;
        }

        .original {
            font-size: 14px;
            color: #999;
            text-decoration: line-through;
            margin-left: 8px;
        }
    }

    .plan-desc {
        font-size: 13px;
        color: #666;
    }

    .plan-tag {
        position: absolute;
        top: -10px;
        right: -10px;
    }
}

.pay-section {
    text-align: center;
    margin-top: 35px;

    .el-button {
        width: 200px;
        height: 45px;
        font-size: 16px;
    }

    .pay-tip {
        margin-top: 15px;
        font-size: 12px;
        color: #999;
    }
}

.vip-benefits {
    padding: 30px 0;

    h3 {
        text-align: center;
        margin-bottom: 25px;
        color: #333;
    }
}

.benefit-list {
    display: flex;
    justify-content: center;
    gap: 50px;
    flex-wrap: wrap;
}

.benefit-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;

    span {
        font-size: 14px;
        color: #666;
    }
}
</style>
