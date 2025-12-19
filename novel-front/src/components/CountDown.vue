<template>
  <div>
    <p v-if="msTime.show" style="margin: 0">
      <span v-if="tipShow">{{ tipText }}:</span>
      <span v-if="!tipShow">{{ tipTextEnd }}:</span>
      <span v-if="Number(msTime.day) > 0">
        <span>{{ msTime.day }}</span>
        <i>{{ dayTxt }}</i>
      </span>
      <span>{{ msTime.hour }}</span>
      <i>{{ hourTxt }}</i>
      <span>{{ msTime.minutes }}</span>
      <i>{{ minutesTxt }}</i>
      <span>{{ msTime.seconds }}</span>
      <i>{{ secondsTxt }}</i>
    </p>
    <p v-if="!msTime.show">{{ endText }}</p>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted } from 'vue'

interface TimeData {
  show: boolean
  day: number | string
  hour: string
  minutes: string
  seconds: string
}

interface Props {
  tipText?: string
  tipTextEnd?: string
  id?: string
  currentTime?: number
  startTime: number
  endTime: number
  endText?: string
  dayTxt?: string
  hourTxt?: string
  minutesTxt?: string
  secondsTxt?: string
  secondsFixed?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  tipText: '距离开始',
  tipTextEnd: '距离结束',
  id: '1',
  currentTime: 0,
  endText: '已结束',
  dayTxt: ':',
  hourTxt: ':',
  minutesTxt: ':',
  secondsTxt: ':',
  secondsFixed: false,
})

const emit = defineEmits<{
  startCallback: [show: boolean]
  endCallback: [show: boolean]
}>()

const tipShow = ref(true)
const msTime = ref<TimeData>({
  show: false,
  day: '',
  hour: '',
  minutes: '',
  seconds: '',
})

let star = 0
let end = 0
let current = 0
let timer: number | null = null

const init = () => {
  // 判断是秒还是毫秒
  star = props.startTime.toString().length === 10 ? props.startTime * 1000 : props.startTime
  end = props.endTime.toString().length === 10 ? props.endTime * 1000 : props.endTime

  if (props.currentTime) {
    current = props.currentTime.toString().length === 10 ? props.currentTime * 1000 : props.currentTime
  } else {
    current = new Date().getTime()
  }

  if (end < current) {
    // 活动已结束
    msTime.value.show = false
    endMessage()
  } else if (current < star) {
    // 活动尚未开始
    tipShow.value = true
    setTimeout(() => {
      runTime(star, current, startMessage)
    }, 1)
  } else if ((end > current && star < current) || star === current) {
    // 活动进行中
    tipShow.value = false
    msTime.value.show = true
    emit('startCallback', msTime.value.show)
    setTimeout(() => {
      runTime(end, current, endMessage, true)
    }, 1)
  }
}

const runTime = (
  startTime: number,
  endTime: number,
  callFun: () => void,
  type: boolean = false
) => {
  let timeDistance = startTime - endTime
  if (timeDistance > 0) {
    msTime.value.show = true
    msTime.value.day = Math.floor(timeDistance / 86400000)
    timeDistance -= (msTime.value.day as number) * 86400000
    msTime.value.hour = String(Math.floor(timeDistance / 3600000)).padStart(2, '0')
    timeDistance -= parseInt(msTime.value.hour) * 3600000
    msTime.value.minutes = String(Math.floor(timeDistance / 60000)).padStart(2, '0')
    timeDistance -= parseInt(msTime.value.minutes) * 60000
    msTime.value.seconds = String(Math.floor(timeDistance / 1000)).padStart(2, '0')

    const startFunc = Date.now()
    const endFunc = Date.now()
    const diffPerFunc = endFunc - startFunc

    timer = window.setTimeout(() => {
      if (type) {
        runTime(end, (endTime += 1000), callFun, true)
      } else {
        runTime(star, (endTime += 1000), callFun)
      }
    }, 1000 - diffPerFunc)
  } else {
    callFun()
  }
}

const startMessage = () => {
  tipShow.value = false
  emit('startCallback', msTime.value.show)
  setTimeout(() => {
    runTime(end, star, endMessage, true)
  }, 1)
}

const endMessage = () => {
  msTime.value.show = false
  if (props.currentTime && props.currentTime <= 0) {
    return
  }
  emit('endCallback', msTime.value.show)
}

watch(
  () => props.currentTime,
  () => {
    if (timer) {
      clearTimeout(timer)
    }
    init()
  }
)

onMounted(() => {
  init()
})

onUnmounted(() => {
  if (timer) {
    clearTimeout(timer)
  }
})
</script>

<style scoped>
span {
  font-weight: bold;
}

i {
  font-style: normal;
  margin: 0 5px;
}
</style>

