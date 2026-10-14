import { defineStore } from 'pinia'
import { useSession } from './session'
import * as station from '@/views/message/station-letter/api'
import * as announcement from '@/views/message/announcement/api'

export const useMessageCenter = defineStore('messageCenter', {
  state: () => ({
    sessionId: '', stationUnread: 0, announcementUnread: 0
  }),
  getters: {
    unread: state => state.stationUnread + state.announcementUnread,
    stationPermissions() {
      const session = useSession()
      return {
        canCreate: session.hasPermission('message_station_add')
      }
    },
    announcementPermissions() {
      const session = useSession()
      return {
        canCreate: session.hasPermission('message_announcement_add'),
        canEdit: session.hasPermission('message_announcement_edit'),
        canDelete: session.hasPermission('message_announcement_del'),
        canSend: session.hasPermission('message_announcement_edit')
      }
    }
  },
  actions: {
    reset() {
      this.$reset()
      this.sessionId = useSession().current?.idleId || ''
    },
    async refresh() {
      const id = useSession().current?.idleId
      if (this.sessionId !== id) this.reset()
      if (!id) return
      const counts = await Promise.allSettled([station.unread(), announcement.unread()])
      if (useSession().current?.idleId !== id) return
      if (counts[0].status === 'fulfilled') this.stationUnread = Math.max(0, Number(counts[0].value) || 0)
      if (counts[1].status === 'fulfilled') this.announcementUnread = Math.max(0, Number(counts[1].value) || 0)
    }
  }
})
