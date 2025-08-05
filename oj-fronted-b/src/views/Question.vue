<template>
    <el-form inline=true> <!-- 行内展示 -->
        <el-form-item>
            <Selector v-model="params.difficulty" placeholder="请选择题⽬难度" style="width: 200px;"></Selector>
        </el-form-item>
        <el-form-item>
            <el-input v-model="params.title" placeholder="请您输⼊要搜索的题⽬标题" />
        </el-form-item>
        <el-form-item>
            <el-button plain @click="onSearch">搜索</el-button>
            <el-button plain type="info" @click="onReset">重置</el-button>
            <el-button plain type="primary" :icon="Plus" @click="onAddQuestion">添加题⽬</el-button>
        </el-form-item>
    </el-form>
    <el-table height="526px" :data="questionList">
        <el-table-column prop="questionId" width="180px" label="题⽬id" />
        <el-table-column prop="title" label="题⽬标题" />
        <el-table-column prop="difficulty" label="题⽬难度" width="90px">
            <template #default="{ row }">
                <div v-if="row.difficulty === 1" style="color:#32CD32;">简单</div>
                <div v-if="row.difficulty === 2" style="color:#FE7909;">中等</div>
                <div v-if="row.difficulty === 3" style="color:#FF0000;">困难</div>
            </template>
        </el-table-column>
        <el-table-column prop="createName" label="创建⼈" width="140px" />
        <el-table-column prop="createTime" label="创建时间" width="180px" />
        <el-table-column label="操作" width="100px" fixed="right">
            <template #default="{ row }">
                <el-button type="text" @click="onEdit(row.questionId)">编辑 </el-button>
                <el-button type="text" class="red" @click="onDelete(row.questionId)">删除</el-button>
            </template>
        </el-table-column>
    </el-table>
    <el-pagination
      background
      :page-sizes="[5, 10, 15, 20]"
      size="small"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      v-model:current-page ="params.pageNum"
      v-model:page-size = "params.pageSize"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
    <question-drawer ref="questionEditRef" @seccess="onSuccess"></question-drawer>
</template>
<script setup>
import { Plus } from "@element-plus/icons-vue"
import Selector from "@/components/QuestionSelector.vue"
import { nextTick, reactive ,ref} from 'vue'
import { getQuestionListService ,delQuestionService} from "@/api/question"
import QuestionDrawer from "@/components/QuestionDrawer.vue"
import { ElMessage, ElMessageBox } from 'element-plus'

const params = reactive({
    pageNum:1,
    pageSize: 10,
    difficulty: '',
    title: ''
})
const questionList = ref([])
const total = ref(0)

async function getQuestionList() {
    const result = await getQuestionListService(params)
    console.log(result)
    questionList.value = result.rows
    total.value = result.total
}
getQuestionList()

async function handleSizeChange(){
    //console.log("页数改变",newSize)
    //params.pageSize = newSize
    params.pageNum = 1
    //console.log(params.pageSize,params.pageNum)
    getQuestionList()
}

async function handleCurrentChange(){
    //console.log("选了新的页面",newPage)
    // params.pageNum = newPage
    getQuestionList()
}

function onSearch (){
    params.pageNum = 1
    getQuestionList()
}

function onReset(){
    params.pageNum = 1
    params.pageSize = 10
    params.difficulty = ''
    params.title = ''
    getQuestionList()
}

const questionEditRef = ref()

function onAddQuestion(){
    questionEditRef.value.open();
}

function onSuccess(service){
    if(service === 'add'){
        params.pageNum = 1;
    }
    getQuestionList();
}

function onEdit(questionId) {
    questionEditRef.value.open(questionId)
}

function onDelete(questionId){
  ElMessageBox.confirm(
    '你确定要删除吗?',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
        ElMessage({ type: 'success', message: '题目删除成功',});
        await delQuestionService(questionId);
        params.pageNum = 1;
        getQuestionList();
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '取消删除',
      })
    });
}

</script>