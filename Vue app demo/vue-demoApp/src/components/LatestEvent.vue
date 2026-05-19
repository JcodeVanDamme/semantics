<template>
  <div class="latest-event">
    <div class="event-header">

      <div class="section-title">
        LATEST EVENT
      </div>

      <button>
        VIEW HISTORY
      </button>

    </div>

    <p class="event-description">
      Triple Store Actions resulting from the recent change of
      Ruler of the State of
      <span>STATENAME</span>
      from
      <span>RULERNAME</span>
      to
      <span>RULERNAME</span>
    </p>

    <table>
      <thead>
        <tr>
          <th>SUBJECT</th>
          <th>PREDICATE</th>
          <th>OBJECT</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in 3" :key="item">
          <td>Subject</td>
          <td>Predicate</td>
          <td>Object</td>
        </tr>
      </tbody>
    </table>

  </div>
</template>

<script setup>
</script>