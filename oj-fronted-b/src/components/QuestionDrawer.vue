<template>
    <el-drawer v-model="visibleDrawer" :destroy-on-close="true" :with-header = "false" size = "50%">
        <el-form :model="formModel" ref="formRef">
            <el-form-item label="题⽬标题:">
                <el-input style="width:387px !important" v-model="formQuestion.title" placeholder="请输⼊标题"></el-input>
            </el-form-item>
            <el-form-item label="题⽬难度:">
                <question-selector style="width:387px !important" v-model="formQuestion.difficulty" width="100%" placeholder="请选择题⽬难度"></question-selector>
            </el-form-item>
            <el-form-item label="时间限制（单位毫秒）:">
                <el-input style="width:300px !important" v-model="formQuestion.timeLimit" placeholder="请输⼊时间限制"></el-input>
            </el-form-item>
            <el-form-item label="空间限制（单位字节）:">
                <el-input style="width:300px !important" v-model="formQuestion.spaceLimit" placeholder="请输⼊空间限制"></el-input>
            </el-form-item>
            <el-form-item label="题⽬内容:">
                <div class="editor">
                    <quill-editor placeholder="请输⼊题⽬内容" v-model:content="formQuestion.content" content-type="html"></quill-editor>
                </div>
            </el-form-item>
            <el-form-item label="题⽬⽤例:">
                <el-input style="width:387px !important" v-model="formQuestion.questionCase" placeholder="请输⼊题⽬⽤例"></el-input>
            </el-form-item>
            <el-form-item label="默认代码块:">
                <code-editor ref="defaultCodeRef" @update:value="handleEditorContent"></code-editor>
            </el-form-item>
            <el-form-item label="main函数:">
                <code-editor ref="mainFucRef" @update:value="handleEditorMainFunc"></code-editor>
            </el-form-item>
            <el-form-item>
                <el-button class="question-button" type="primary" plain @click="onSubmit()">添加此题目</el-button>
            </el-form-item>
        </el-form>
    </el-drawer>
</template>
<script setup>
import { QuillEditor } from '@vueup/vue-quill';
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import CodeEditor from './CodeEditor.vue';
import { ref ,reactive} from 'vue';
import QuestionSelector from './QuestionSelector.vue';
import {addQuestionService,getQuestionDetailService,editQuestionService} from '@/api/question'

const visibleDrawer = ref(false)
const formQuestion = reactive({
  questionId: '',
  title: '',
  difficulty: '',
  content: '',
  questionCase: '',
  timeLimit: '',
  spaceLimit: '',
  defaultCode: '',
  mainFuc: ''
})

const defaultCodeRef = ref()
const mainFucRef = ref()

async function open(questionId){
    visibleDrawer.value = true;
    for (const key in formQuestion) {
        if (formQuestion.hasOwnProperty(key)) {
            formQuestion[key] = '';
        }   
    }
    if(questionId){
        const questionDetail = await getQuestionDetailService(questionId);
        console.log(questionDetail);
        Object.assign(formQuestion,questionDetail.data);
        defaultCodeRef.value.setAceCode(formQuestion.defaultCode)
        mainFucRef.value.setAceCode(formQuestion.mainFuc)
    }
}

// 把open这个函数暴露出去
defineExpose({
    open
})

function validate() {
  let msg = ''
  if (!formQuestion.title) {
    msg = '请添加题目标题'
  } else if (formQuestion.difficulty === '') {
    msg = '请选择题⽬难度'
  } else if (!formQuestion.timeLimit) {
    msg = '请输⼊时间限制'
  } else if (!formQuestion.spaceLimit) {
    msg = '请输⼊空间限制'
  } else if (!formQuestion.content) {
    msg = '请输⼊题⽬内容信息'
  } else if (!formQuestion.questionCase) {
    msg = '请输⼊题⽬⽤例'
  } else if (!formQuestion.defaultCode) {
    msg = '请输⼊默认代码'
  } else if (!formQuestion.mainFuc) {
    msg = '请输⼊main函数'
  } else {
    msg = ''
  }
  return msg
}

// const emit = defineEmits(['seccess']); 好像不用传 seccess这个参数也行
const emit = defineEmits();
async function onSubmit() {
  const errorMessage = validate() // 保证信息不能为空
  if (errorMessage) {
    ElMessage.error(errorMessage);
    return false
  }
  const fd = new FormData()
  for (let key in formQuestion) {
    fd.append(key, formQuestion[key])  // 把数据全部放进表单里
  }
  if(formQuestion.questionId){
    // 修改题目
    await editQuestionService(fd);
    ElMessage.success('编辑成功');
    emit('seccess','edit');
  }else{
    // 添加题目
    await addQuestionService(fd)
    ElMessage.success('添加成功')
    emit('seccess','add');
  }
  visibleDrawer.value = false // 抽屉收起来
}

// 把编译器的代码赋值到我们的响应式数据里面
function handleEditorContent(content){
    formQuestion.defaultCode = content;
}
function handleEditorMainFunc(content){
    formQuestion.mainFuc = content;
}

</script>
<style lang="scss">
.question-button {
    width: 200px;
}
</style>