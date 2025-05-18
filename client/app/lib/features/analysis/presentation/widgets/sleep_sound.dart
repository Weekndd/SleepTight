import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:just_audio/just_audio.dart';
import 'package:sleep_tight/core/config/theme/color.dart';

class SleepSound extends ConsumerStatefulWidget {
  const SleepSound({super.key});

  @override
  ConsumerState<SleepSound> createState() => _SleepSoundState();
}

class _SleepSoundState extends ConsumerState<SleepSound> {
  final Map<int, AudioPlayer> _players = {};

  @override
  void dispose() {
    for (var player in _players.values) {
      player.dispose();
    }
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final data = mockSleepSoundData;
    final sounds = data['sounds'] as List<dynamic>? ?? [];

    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            '감지 된 이상 현상',
            style: TextStyle(
              color: AppColors.white,
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 16),
          for (var sound in (sounds)) _buildSoundItem(sound),
        ],
      ),
    );
  }

  Widget _buildSoundItem(Map<String, dynamic> sound) {
    final soundId = sound['soundId'] as int;
    final clipUrl = sound['clipUrl'] as String;
    final start = sound['soundStartTime'];
    final end = sound['soundEndTime'];
    final events = sound['events'] as List<dynamic>;

    _players.putIfAbsent(soundId, () {
      final player = AudioPlayer();
      player.setUrl(clipUrl);
      return player;
    });

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          height: 60,
          margin: const EdgeInsets.symmetric(vertical: 8),
          padding: const EdgeInsets.symmetric(horizontal: 12),
          decoration: BoxDecoration(
            color: Colors.white10,
            borderRadius: BorderRadius.circular(8),
          ),
          child: Row(
            children: [
              IconButton(
                icon: const Icon(Icons.play_arrow, color: Colors.white),
                onPressed: () => _players[soundId]!.play(),
              ),
              const Text('오디오 재생', style: TextStyle(color: Colors.white)),
            ],
          ),
        ),
        Text(
          '$start ~ $end',
          style: const TextStyle(color: Colors.white60, fontSize: 12),
        ),
        const SizedBox(height: 8),
        for (var event in events)
          Text(
            '${_getAnomalyLabel(event['anomaly'])} (${_formatSec(event['eventStartSec'])} ~ ${_formatSec(event['eventEndSec'])})',
            style: const TextStyle(color: Colors.white, fontSize: 13),
          ),
        const SizedBox(height: 20),
      ],
    );
  }

  String _getAnomalyLabel(String anomaly) {
    switch (anomaly) {
      case 'snore':
        return '코골이';
      case 'talk':
        return '잠꼬대';
      case 'cough':
        return '기침';
      default:
        return anomaly;
    }
  }

  String _formatSec(num sec) {
    final minutes = (sec ~/ 60).toString().padLeft(2, '0');
    final seconds = (sec % 60).toInt().toString().padLeft(2, '0');
    return '$minutes:$seconds';
  }
}

const mockSleepSoundData = {
  "reportId": 1,
  "date": "2025-04-27",
  "sounds": [
    {
      "soundId": 1,
      "soundStartTime": "04:52:12",
      "soundEndTime": "04:52:22",
      "clipUrl": "https://s3.amazonaws.com/audio-prod/.../events/1023.opus",
      "events": [
        {
          "eventId": "ffggh1asdjsdzxc",
          "anomaly": "snore",
          "eventStartSec": 0,
          "eventEndSec": 2,
          "confidence": 0.92,
        },
        {
          "eventId": "asdgh1asdj1hjk5h",
          "anomaly": "talk",
          "eventStartSec": 2,
          "eventEndSec": 6,
          "confidence": 0.98,
        },
      ],
    },
  ],
};
