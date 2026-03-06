<template>
    <div class="container">
        <h3>个人资料</h3>
        <el-form :model="chef" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
            <el-form-item label="姓名" prop="name">
                <el-input v-model="chef.name" placeholder="请输入姓名"></el-input>
            </el-form-item>


            <el-form-item label="头像">
                <el-upload class="avatar-uploader" action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
                    :show-file-list="false" :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
                    <img v-if="imageUrl" :src="imageUrl" class="avatar" />
                    <el-icon v-else class="avatar-uploader-icon">
                        <Plus />
                    </el-icon>
                </el-upload>
            </el-form-item>
            <el-form-item label="电话" prop="phone">
                <el-input v-model="chef.phone" placeholder="请输入电话号码"></el-input>
            </el-form-item>
            <el-form-item label="身份证号" prop="sfzid">
                <el-input v-model="chef.sfzid" placeholder="请输入身份证号"></el-input>
            </el-form-item>
            <el-form-item label="常驻地址">
                <el-input 
                    v-model="chef.address.fullAddress" 
                    placeholder="请输入地址或点击右侧图标选择"
                    clearable
                >
                    <template #append>
                        <el-button :icon="Position" @click="openMapPicker" title="地图选点">
                            地图选点
                        </el-button>
                    </template>
                </el-input>
            </el-form-item>
            <el-form-item label="简介">
                <el-input v-model="chef.introduction" type="textarea" placeholder="请输入简介"></el-input>
            </el-form-item>
            <el-form-item label="菜系">

                <el-checkbox-group v-model="chef.dishkinds">
                    <el-checkbox label="LuCai">鲁菜</el-checkbox>
                    <el-checkbox label="ChuanCai">川菜</el-checkbox>
                    <el-checkbox label="YueCai">粤菜</el-checkbox>
                    <el-checkbox label="SuCai">苏菜</el-checkbox>
                    <el-checkbox label="MinCai">闽菜</el-checkbox>
                    <el-checkbox label="ZheCai">浙菜</el-checkbox>
                    <el-checkbox label="XiangCai">湘菜</el-checkbox>
                    <el-checkbox label="HuiCai">徽菜</el-checkbox>

                </el-checkbox-group>
            </el-form-item>
            <el-form-item label="评分">
                <el-rate v-model="star" disabled  text-color="#ff9900" />
            </el-form-item>



            <el-form-item label="执业资质">
                <el-upload class="upload-demo" drag
                    action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15" multiple>
                    <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                    <div class="el-upload__text">
                        相关行业执业证书 <em>点击上传</em>
                    </div>
                    <template #tip>
                        <div class="el-upload__tip">
                            jpg/png文件，且不超过500
                        </div>
                    </template>
                </el-upload>
            </el-form-item>
            <el-form-item>

                <el-button type="primary" @click="submitForm('ruleForm')">保存</el-button>
                <el-button @click="resetForm('ruleForm')">重置</el-button>
            </el-form-item>
        </el-form>

        <!-- 地图选点对话框 -->
        <el-dialog v-model="mapDialogVisible" title="选择位置" width="800px">
            <div ref="mapContainer" class="map-container"></div>
            <template #footer>
                <el-button @click="mapDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="confirmLocation">确定</el-button>
            </template>
        </el-dialog>

    </div>
</template>
<script>
import { Position, UploadFilled, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

export default {
    components: {
        Position,
        UploadFilled,
        Plus
    },
    data() {
        return {
            Position, // 让模板可以访问 Position 图标
            imageUrl: '',
            star: 3.7,
            chef: {
                name: '',// 姓名
                phone: '',// 电话
                sfzid: '',// 身份证号
                address: {
                    fullAddress: '',// 详细地址
                    province: '',// 省份
                    city: '',// 城市
                    district: '',// 区县
                    street: '',// 街道
                    streetNumber: '',// 门牌号
                    longitude: '',// 经度
                    latitude: ''// 纬度
                },// 地址
                introduction: '',// 简介
                dishkinds: [],// 菜系
                avatar: '',// 头像
                status: '',// 状态
                certificate: '',// 证书
                star: 0// 评分
            },
            rules: {
                name: [
                    { required: true, message: '请输入姓名', trigger: 'blur' },
                    { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
                ],
                phone: [
                    { required: true, message: '请输入电话号码', trigger: 'blur' },
                    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的电话号码', trigger: 'blur' }
                ],
                address: [
                    { required: true, message: '请输入地址', trigger: 'blur' },
                    { min: 5, max: 50, message: '长度在 5 到 50 个字符', trigger: 'blur' }
                ],
                sfzid: [
                    { required: true, message: '请输入身份证号', trigger: 'blur' },
                    { pattern: /^\d{15}$|^\d{18}$|^\d{17}(\d|X|x)$/, message: '请输入有效的身份证号', trigger: 'blur' }
                ]
            },
            // 地图相关
            mapDialogVisible: false,
            map: null,
            marker: null,
            selectedPoint: null
        };
    },
    methods: {
        // 打开地图选择器
        openMapPicker() {
            this.mapDialogVisible = true;
            // 对话框打开后延迟初始化地图，确保容器已渲染且有正确尺寸
            this.$nextTick(() => {
                setTimeout(() => {
                    this.initMapPicker();
                }, 100);
            });
        },
        // 初始化地图选择器
        initMapPicker() {
            if (!this.$refs.mapContainer) {
                console.error('地图容器未找到');
                return;
            }
            
            if (!window.BMapGL && !window.BMap) {
                ElMessage.error('百度地图未加载');
                return;
            }

            // 优先使用 BMapGL（WebGL 版）
            const isGL = !!window.BMapGL;
            const BMap = isGL ? window.BMapGL : window.BMap;
            console.log('使用地图版本:', isGL ? 'BMapGL' : 'BMap');

            // 创建地图实例
            this.map = new BMap.Map(this.$refs.mapContainer);
            
            // 默认中心点（北京）
            const point = new BMap.Point(116.404, 39.915);

            // 初始化地图
            this.map.centerAndZoom(point, 15);
            this.map.enableScrollWheelZoom(true);
            this.map.addControl(new BMap.NavigationControl());
            this.map.addControl(new BMap.ScaleControl());

            // 添加点击事件
            this.map.addEventListener('click', (e) => {
                const point = e.point; // 普通版 BMap 直接返回经纬度
                const lng = point.lng;
                const lat = point.lat;
                
                console.log('点击位置坐标:', lng, lat);

                this.selectedPoint = point;

                // 清除旧标记
                if (this.marker) {
                    this.map.removeOverlay(this.marker);
                }

                // 添加新标记（使用自定义图标）
                const markerIcon = new BMap.Icon(
                    'https://api.map.baidu.com/library/SearchInfoWindow/1.5/src/images/marker_red.png',
                    new BMap.Size(22, 40),
                    {
                        offset: new BMap.Size(11, 40),
                        imageAnchor: new BMap.Size(11, 40)
                    }
                );
                this.marker = new BMap.Marker(point, { icon: markerIcon });
                this.map.addOverlay(this.marker);

                // 使用 Geocoder 获取地址
                const geoc = new BMap.Geocoder();
                geoc.getLocation(point, (rs) => {
                    console.log('Geocoder 返回结果:', rs);
                    
                    if (rs && rs.address) {
                        this.chef.address = {
                            fullAddress: rs.address,
                            province: rs.province || '',
                            city: rs.city || '',
                            district: rs.district || '',
                            street: rs.street || '',
                            streetNumber: rs.streetNumber || '',
                            longitude: lng,
                            latitude: lat
                        };
                        console.log('选中地址:', this.chef.address);
                        ElMessage.success('已选择：' + rs.address);
                    } else {
                        console.log('Geocoder 未返回地址');
                        this.chef.address = {
                            fullAddress: `经度:${lng.toFixed(6)}, 纬度:${lat.toFixed(6)}`,
                            longitude: lng,
                            latitude: lat
                        };
                        ElMessage.warning('未获取到详细地址，已保存坐标');
                    }
                });
            });
        },
        // 确认选择的位置
        confirmLocation() {
            if (!this.selectedPoint) {
                ElMessage.warning('请选择一个位置');
                return;
            }
            this.mapDialogVisible = false;
            ElMessage.success('地址已选择');
        },
        submitForm(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    alert('submit!');
                } else {
                    console.log('error submit!!');
                    return false;
                }
            });
        },
        resetForm(formName) {
            this.$refs[formName].resetFields();
        },
        handleAvatarSuccess(res, file) {
            this.imageUrl = URL.createObjectURL(file.raw);
        },
        beforeAvatarUpload(file) {
            const isJPG = file.type === 'image/jpeg';
            const isLt2M = file.size / 1024 / 1024 < 2;

            if (!isJPG) {
                ElMessage.error('上传头像图片只能是 JPG 格式!');
            }
            if (!isLt2M) {
                ElMessage.error('上传头像图片大小不能超过 2MB!');
            }
            return isJPG && isLt2M;
        }
    }
}
</script>
<style scoped>
.container {
    width: 1000px;
}

.avatar-uploader .avatar {
    width: 178px;
    height: 178px;
    display: block;
}

.avatar-uploader .el-upload {
    border: 1px dashed #409eff;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
    border: 1px dashed #409eff;
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    text-align: center;
}

h3 {
    margin: 20;
    text-align: center;
    justify-content: center;
    display: flex;
}

.map-container {
    width: 100%;
    height: 400px;
    border: 1px solid #ddd;
    border-radius: 4px;
}
</style>