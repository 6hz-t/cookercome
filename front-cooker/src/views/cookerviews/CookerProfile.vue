<template>
    <div class="container">
        <h3>个人资料</h3>
        <!-- {
  "username": "",
  "relName": "",
  "phone": "",
  "password": "",
  "idCardFront": "",
  "idCardBack": "",
  "gender": 0,
  "address": "",
  "lon": 0,
  "lat": 0,
  "introduction": "",
  "avatar": "",
  "idCardFrontFile": "",
  "idCardBackFile": "",
  "qualificationFile": ""
} -->
        <el-form :model="chef" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
            <el-form-item label="姓名" prop="name">
                <el-input v-model="chef.name" placeholder="请输入姓名"></el-input>
            </el-form-item>
            <el-form-item label="性别" prop="sex">
                <el-radio-group v-model="chef.sex">
                    <el-radio label="1">男</el-radio>
                    <el-radio label="0">女</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="身份证号" prop="sfzid">
                <el-input v-model="chef.sfzid" placeholder="请输入身份证号"></el-input>
            </el-form-item>
            <el-form-item label="身份证正面">
                <el-upload class="avatar-uploader" action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
                    :show-file-list="false" :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
                    <img v-if="imageUrl" :src="imageUrl" class="avatar" />
                    <el-icon v-else class="avatar-uploader-icon">
                        <Plus />
                    </el-icon>
                </el-upload>
            </el-form-item>
            <el-form-item label="身份证反面">
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

            <el-form-item label="常驻地址">
                <el-input v-model="chef.address.fullAddress" placeholder="请输入地址或点击右侧图标选择" clearable>
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
            

            <el-form-item label="执业资质">
                <el-upload class="upload-demo" drag
                    action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15" multiple>
                    <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                    <div class="el-upload__text">
                        相关行业执业证书 <em>点击上传</em>
                    </div>
                    <template #tip>
                        <div class="el-upload__tip">
                            jpg/png 文件，且不超过 500
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
<!--         /*
        * 姓名
        * 手机号
        * 身份证正面
        * 身份证反面
        * 性别
        * 身份证号
        * 头像
        * 注册时间
        * 状态（0：在线，1：离线）
        * 评分
        * 位置经度
        * 位置纬度
        * 位置全址
        * 简介
        * 审核状态
        * 菜系
  
        *
        *
        * */ -->

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
            imageUrl: "https://example.com/avatar.jpg",
            star: 4.8,
            chef: {
                "username": "chef123",
                "relName": "张师傅",
                "phone": "13800138000",
                "password": "********",
                "idCardFront": "https://example.com/idcard-front.jpg",
                "idCardBack": "https://example.com/idcard-back.jpg",
                "gender": 1,
                "address": {
                    "fullAddress": "湖北省武汉市洪山区珞喻路129号",
                    "province": "湖北省",
                    "city": "武汉市",
                    "district": "洪山区",
                    "street": "珞喻路",
                    "streetNumber": "129号",
                    "longitude": 114.398833,
                    "latitude": 30.506859
                },
                "lon": 114.398833,
                "lat": 30.506859,
                "introduction": "拥有20年烹饪经验，擅长川菜和粤菜，曾获得多项烹饪比赛奖项。注重食材新鲜，烹饪手法独特，深受顾客喜爱。服务态度热情周到，善于根据客户需求定制个性化菜单。",
                "avatar": "https://example.com/avatar.jpg",
                "idCardFrontFile": "idcard_front.jpg",
                "idCardBackFile": "idcard_back.jpg",
                "qualificationFile": "qualification.pdf",
                "dishkinds": ["ChuanCai", "YueCai", "LuCai", "SuCai"],
                "registerTime": "2022-05-15",
                "status": 0,
                "score": 4.8,
                "auditStatus": 1
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
                sfzid: [
                    { required: true, message: '请输入身份证号', trigger: 'blur' },
                    { pattern: /^\d{15}$|^\d{18}$|^\d{17}(\d|X|x)$/, message: '请输入有效的身份证号', trigger: 'blur' }
                ]
            },
            mapDialogVisible: false,
            map: null,
            marker: null,
            selectedPoint: null
        };
    },
    methods: {
        /**
         * 将百度地图墨卡托坐标转换为经纬度坐标
         * @param {number} mercatorX - 墨卡托 X 坐标（米）
         * @param {number} mercatorY - 墨卡托 Y 坐标（米）
         * @returns {{lng: number, lat: number}} 返回包含经度和纬度的对象
         */
        mercatorToLatLng(mercatorX, mercatorY) {
            const x = mercatorX / 20037508.34 * 180;
            let y = mercatorY / 20037508.34 * 180;
            y = 180 / Math.PI * (2 * Math.atan(Math.exp(y * Math.PI / 180)) - Math.PI / 2);
            return { lng: x, lat: y };
        },
        openMapPicker() {
            this.mapDialogVisible = true;
            this.$nextTick(() => {
                setTimeout(() => {
                    this.initMapPicker();
                }, 100);
            });
        },
        /**
         * 初始化百度地图选择器
         * 创建地图实例并设置点击事件监听，当用户点击地图时：
         * - 将墨卡托坐标转换为经纬度坐标
         * - 在点击位置添加标记点
         * - 通过逆地理编码获取地址信息
         * @returns {void} 无返回值
         */
        initMapPicker() {
            if (!this.$refs.mapContainer) {
                console.error('地图容器未找到');
                return;
            }

            if (!window.BMapGL) {
                ElMessage.error('百度地图 BMapGL 未加载，请检查 index.html 配置');
                return;
            }

            const BMap = window.BMapGL;
            console.log('使用地图版本：BMapGL');

            // 创建地图实例
            this.map = new BMap.Map(this.$refs.mapContainer);

            // 默认中心点（武汉）
            const point = new BMap.Point(114.438217, 30.464676);
            this.map.centerAndZoom(point, 10);
            this.map.enableScrollWheelZoom(true);
            this.map.addControl(new BMap.NavigationControl());
            this.map.addControl(new BMap.ScaleControl());

            /**
             * 地图点击事件处理
             * 处理用户点击地图后的坐标获取、标记点更新和地址解析
             */
            this.map.addEventListener('click', (e) => {
                let lngLatPoint = null;
                let lng, lat;

                // BMapGL 点击事件返回的 e.point 是墨卡托坐标（单位：米），需要转换为经纬度
                if (e.point) {
                    console.log('e.point (墨卡托):', e.point);
                    console.log('e.point.lng:', e.point?.lng, 'e.point.lat:', e.point?.lat);

                    // 将墨卡托坐标转换为经纬度
                    const result = this.mercatorToLatLng(e.point.lng, e.point.lat);
                    lng = result.lng;
                    lat = result.lat;

                    // 创建新的经纬度点对象
                    lngLatPoint = new BMap.Point(lng, lat);
                    console.log('转换后的经纬度:', lng, lat);
                }

                if (!lngLatPoint) {
                    console.error('无法获取经纬度坐标');
                    return;
                }

                console.log('点击位置坐标 (经纬度):', lng, lat);
                this.selectedPoint = lngLatPoint;


                console.log('marker', this.marker);

                // 清除旧标记
                if (this.marker) {
                    this.map.removeOverlay(this.marker);
                }

                // 添加新标记
                this.marker = new BMap.Marker(lngLatPoint);
                this.map.addOverlay(this.marker);

                // 逆地理编码获取地址
                const geoc = new BMap.Geocoder();
                geoc.getLocation(lngLatPoint, (rs) => {
                    console.log('Geocoder 完整返回:', rs);
                    console.log('rs.address:', rs?.address);
                    console.log('rs.formatted_address:', rs?.formatted_address);
                    console.log('rs.addressComponent:', rs?.addressComponent);
                    console.log('rs.content:', rs?.content);
                    this.handleGeocodeResult(rs, lng, lat);
                });
            });
        },
        handleGeocodeResult(rs, lng, lat) {
            console.log('handleGeocodeResult 收到:', rs);

            // BMapGL 返回的是 addressComponent 而不是 addressComponents
            const comp = rs.addressComponent || rs.addressComponents || {};
            const address = rs.address || rs.formatted_address || '';

            if (address) {
                this.chef.address = {
                    fullAddress: address,
                    province: comp.province || '',
                    city: comp.city || '',
                    district: comp.district || '',
                    street: comp.street || '',
                    streetNumber: comp.street_number || '',
                    longitude: lng,
                    latitude: lat
                };
                console.log('选中地址:', this.chef.address);
                ElMessage.success('已选择：' + address);
            } else {
                this.chef.address = {
                    fullAddress: `经度:${lng.toFixed(6)}, 纬度:${lat.toFixed(6)}`,
                    longitude: lng,
                    latitude: lat
                };
                ElMessage.warning('未获取到详细地址，已保存坐标');
            }
        },
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
    /* 虚线框 */
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
    margin: 20px;
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
