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
            <el-form-item label="姓名" prop="relName">
                <el-input v-model="chef.relName" placeholder="请输入姓名"></el-input>
            </el-form-item>
            <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="chef.gender">
                    <el-radio :label="1">男</el-radio>
                    <el-radio :label="0">女</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="身份证号" prop="idCard">
                <el-input v-model="chef.idCard" placeholder="请输入身份证号"></el-input>
            </el-form-item>
            <el-form-item label="身份证正面">
                <el-upload 
                    class="avatar-uploader" 
                    :http-request="(options) => uploadToOSS(options, 'idCardFront')"
                    :show-file-list="false" 
                    :before-upload="beforeAvatarUpload">
                    <img v-if="chef.idCardFront" :src="chef.idCardFront" class="avatar" />
                    <el-icon v-else class="avatar-uploader-icon">
                        <Plus />
                    </el-icon>
                </el-upload>
            </el-form-item>
            <el-form-item label="身份证反面">
                <el-upload 
                    class="avatar-uploader" 
                    :http-request="(options) => uploadToOSS(options, 'idCardBack')"
                    :show-file-list="false" 
                    :before-upload="beforeAvatarUpload">
                    <img v-if="chef.idCardBack" :src="chef.idCardBack" class="avatar" />
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
                <el-upload 
                    class="upload-demo" 
                    drag
                    :http-request="(options) => uploadToOSS(options, 'qualification')"
                    multiple>
                    <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                    <div class="el-upload__text">
                        相关行业执业证书 <em>点击上传</em>
                    </div>
                    <template #tip>
                        <div class="el-upload__tip">
                            jpg/png 文件，且不超过 500kb
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
          </el-input>
        </el-form-item>

        <el-form-item label="坐标">
          <div class="coord-row">
            <el-input :model-value="latitudeText" disabled />
            <el-input :model-value="longitudeText" disabled />
            <el-button @click="mapDialogVisible = true">重新选取</el-button>
          </div>
        </el-form-item>

        <el-form-item label="从业年限">
          <el-input-number v-model="form.experienceYears" :min="0" :step="1" style="width: 100%" />
        </el-form-item>

        <el-form-item label="厨师等级">
          <el-input-number v-model="form.chefLevel" :min="0" :step="1" style="width: 100%" />
        </el-form-item>

        <el-form-item label="最低服务价格">
          <el-input-number v-model="form.minPrice" :min="0" :precision="2" :step="10" style="width: 100%" />
        </el-form-item>

        <el-form-item label="个人介绍">
          <el-input v-model="form.introduction" type="textarea" :rows="3" placeholder="请输入个人介绍" />
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="离线" :value="0" />
            <el-option label="在线" :value="1" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <BaiduMapPickerDialog
      v-model="mapDialogVisible"
      title="地图选点（厨师地址）"
      :initial-location="profileLocation"
      @confirm="handleLocationPicked"
    />
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import OSS from 'ali-oss'
import axios from 'axios'

export default {
    components: {
        Position,
        UploadFilled,
        Plus
    },
    data() {
        return {
            ossClient: null,
            stsCredentials: null, // 存储STS临时凭证
            credentialsExpireTime: 0, // 凭证过期时间
            star: 4.8,
            chef: {
                "username": "chef123",
                "relName": "张师傅",
                "phone": "13800138000",
                "password": "********",
                "idCard": "",
                "idCardFront": "",
                "idCardBack": "",
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
                relName: [
                    { required: true, message: '请输入姓名', trigger: 'blur' },
                    { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
                ],
                phone: [
                    { required: true, message: '请输入电话号码', trigger: 'blur' },
                    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的电话号码', trigger: 'blur' }
                ],
                idCard: [
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
         * 从后端获取STS临时凭证
         * @returns {Promise<Object>} 返回包含accessKeyId、accessKeySecret和stsToken的对象
         */
        async getSTSCredentials() {
            try {
                // 检查凭证是否仍然有效
                if (this.stsCredentials && Date.now() < this.credentialsExpireTime) {
                    console.log('使用缓存的STS凭证');
                    return this.stsCredentials;
                }

                console.log('从后端获取新的STS凭证...');
                // 替换为您的后端API地址
                const response = await axios.get('/api/oss/sts');

                // 假设后端返回的数据格式为：
                // {
                //   code: 200,
                //   data: {
                //     accessKeyId: 'xxx',
                //     accessKeySecret: 'xxx',
                //     stsToken: 'xxx',
                //     expiration: '2023-12-31T12:00:00Z'
                //   }
                // }
                if (response.data.code === 200) {
                    this.stsCredentials = {
                        accessKeyId: response.data.data.accessKeyId,
                        accessKeySecret: response.data.data.accessKeySecret,
                        stsToken: response.data.data.stsToken
                    };

                    // 设置凭证过期时间（提前5分钟过期，避免临界点问题）
                    const expirationTime = new Date(response.data.data.expiration).getTime();
                    this.credentialsExpireTime = expirationTime - 5 * 60 * 1000;

                    console.log('STS凭证获取成功，过期时间:', new Date(this.credentialsExpireTime));
                    return this.stsCredentials;
                } else {
                    throw new Error(response.data.message || '获取STS凭证失败');
                }
            } catch (error) {
                console.error('获取STS凭证失败:', error);
                throw error;
            }
        },
        /**
         * 初始化阿里云OSS客户端
         * 使用STS临时凭证初始化
         */
        async initOSSClient() {
            try {
                // 获取STS临时凭证
                const credentials = await this.getSTSCredentials();

                // 使用STS凭证初始化OSS客户端
                this.ossClient = new OSS({
                    region: 'oss-cn-hangzhou', // 您的OSS区域
                    accessKeyId: credentials.accessKeyId,
                    accessKeySecret: credentials.accessKeySecret,
                    stsToken: credentials.stsToken,
                    bucket: 'lihuahua-cookhome', // 您的OSS存储桶名称
                    secure: true // 使用HTTPS
                });

                console.log('OSS客户端初始化成功');
            } catch (error) {
                console.error('初始化OSS客户端失败:', error);
                throw error;
            }
        },
        /**
         * 上传文件到阿里云OSS
         * @param {Object} options - Element Plus上传组件传递的参数
         * @param {File} options.file - 要上传的文件对象
         * @param {string} fileType - 文件类型，用于标识是身份证正面、反面还是资质证书
         */
        async uploadToOSS(options, fileType) {
            try {
                const file = options.file;
                console.log('开始上传文件:', {
                    fileType,
                    fileName: file.name,
                    fileSize: (file.size / 1024).toFixed(2) + 'KB',
                    fileType: file.type
                });

                // 如果OSS客户端未初始化或凭证即将过期，重新初始化
                if (!this.ossClient || Date.now() >= this.credentialsExpireTime) {
                    console.log('初始化或重新初始化OSS客户端...');
                    await this.initOSSClient();
                }

                // 生成唯一文件名
                const timestamp = new Date().getTime();
                const randomStr = Math.random().toString(36).substring(2);
                const fileName = `${fileType}_${timestamp}_${randomStr}.${file.name.split('.').pop()}`;
                console.log('生成的文件名:', fileName);

                // 上传文件到OSS
                console.log('正在上传到OSS...');
                const result = await this.ossClient.put(fileName, file);
                console.log('OSS上传结果:', result);
                console.log('文件URL:', result.url);

                // 根据文件类型设置对应的URL
                if (fileType === 'idCardFront') {
                    this.chef.idCardFront = result.url;
                    this.chef.idCardFrontFile = fileName;
                    console.log('身份证正面URL已更新:', this.chef.idCardFront);
                } else if (fileType === 'idCardBack') {
                    this.chef.idCardBack = result.url;
                    this.chef.idCardBackFile = fileName;
                    console.log('身份证反面URL已更新:', this.chef.idCardBack);
                } else if (fileType === 'qualification') {
                    this.chef.qualificationFile = result.url;
                    console.log('执业资质URL已更新:', this.chef.qualificationFile);
                }

                console.log('文件上传成功');
                ElMessage.success('文件上传成功');
            } catch (error) {
                console.error('OSS上传失败:', error);
                console.error('错误详情:', {
                    message: error.message,
                    code: error.code,
                    name: error.name
                });

                // 如果是凭证过期错误，清除凭证并提示用户
                if (error.name === 'AccessDeniedError' || error.code === 'AccessDenied') {
                    console.log('凭证可能已过期，清除缓存');
                    this.stsCredentials = null;
                    this.credentialsExpireTime = 0;
                    this.ossClient = null;
                    ElMessage.error('凭证已过期，请重试');
                } else {
                    ElMessage.error('文件上传失败: ' + error.message);
                }
            }
        },
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
            // 由于使用了OSS直传，这个方法已经不再需要
            // 保留是为了兼容性，实际文件上传逻辑在uploadToOSS方法中处理
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
      },
      trigger: 'blur'
    }
  ]
}

const latitudeText = computed(() => (Number(form.latitude) ? Number(form.latitude).toFixed(6) : '未选择'))
const longitudeText = computed(() => (Number(form.longitude) ? Number(form.longitude).toFixed(6) : '未选择'))

const profileLocation = computed(() => ({
  lng: Number(form.longitude || 0),
  lat: Number(form.latitude || 0),
  address: form.detailAddress || ''
}))

function fillForm(data = {}) {
  form.id = Number(data.id || 0)
  form.idText = String(form.id)
  form.userId = String(data.userId || form.userId || '')
  form.realName = data.realName || ''
  form.gender = Number(data.gender || 0)
  form.idCardNo = data.idCardNo || ''
  form.phone = data.phone || ''
  form.detailAddress = data.detailAddress || ''
  form.experienceYears = Number(data.experienceYears || 0)
  form.chefLevel = Number(data.chefLevel || 0)
  form.minPrice = Number(data.minPrice || 0)
  form.introduction = data.introduction || ''
  form.latitude = Number(data.latitude || 0)
  form.longitude = Number(data.longitude || 0)
  form.status = Number(data.status || 0)

  setChefLocation({
    lng: form.longitude,
    lat: form.latitude,
    address: form.detailAddress
  })
}

function handleLocationPicked(location) {
  form.longitude = Number(location.lng || 0)
  form.latitude = Number(location.lat || 0)
  if (location.address) {
    form.detailAddress = location.address
  }
  setChefLocation({
    lng: form.longitude,
    lat: form.latitude,
    address: form.detailAddress
  })
  ElMessage.success('位置已更新')
}

async function loadProfile() {
  const userId = parseChefId()
  if (!userId) {
    ElMessage.warning('请先登录')
    router.replace('/cooker/login')
    return
  }

  loading.value = true
  try {
    form.userId = String(userId)
    const res = await getChefProfile(userId)
    if (res.code !== 200) {
      ElMessage.error(res.message || '加载资料失败')
      return
    }
    fillForm(res.data || { userId: String(userId) })
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || '加载资料失败')
  } finally {
    loading.value = false
  }
}

async function handleSave() {
  if (!form.userId) {
    ElMessage.warning('用户 ID 不能为空')
    return
  }

  // 表单验证
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
  } catch (error) {
    ElMessage.error('请检查表单填写是否正确')
    return
  }

  saving.value = true
  try {
    const payload = {
      id: form.id,
      userId: form.userId,
      realName: form.realName,
      gender: form.gender,
      idCardNo: form.idCardNo,
      phone: form.phone,
      detailAddress: form.detailAddress,
      experienceYears: form.experienceYears,
      chefLevel: form.chefLevel,
      minPrice: form.minPrice,
      introduction: form.introduction,
      latitude: form.latitude,
      longitude: form.longitude,
      status: form.status
    }

    const res = await saveChefProfile(payload)
    if (res.code !== 200) {
      ElMessage.error(res.message || '保存失败')
      return
    }

    fillForm(res.data || payload)
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || error?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(loadProfile)
</script>

<style scoped>
.page {
  max-width: 960px;
  margin: 0 auto;
  padding: 0 20px 20px;
}

.head-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form {
  max-width: 760px;
}

.coord-row {
  width: 100%;
  display: grid;
  grid-template-columns: 1fr 1fr auto;
  gap: 10px;
}

@media (max-width: 768px) {
  .page {
    padding: 0 12px 14px;
  }

  .head-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .coord-row {
    grid-template-columns: 1fr;
  }
}
</style>
