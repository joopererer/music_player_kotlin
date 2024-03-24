package com.jiawei.musicplayer.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jiawei.musicplayer.model.MusicFile
import com.jiawei.musicplayer.model.PlayerViewModel
import com.jiawei.musicplayer.ui.theme.MusicPlayerTheme
import com.jiawei.musicplayer.utils.milliSecondsToTimeString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier, musicList: List<MusicFile>, isLoading: Boolean, playerViewModel: PlayerViewModel = viewModel()) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(text = "${musicList.size} music files found")
                }
            )
        },
    ) {
        val playerState by playerViewModel.playerState.collectAsState()
        var isSeeking by remember{ mutableStateOf(false) }
        var seekProgress by remember{ mutableStateOf(0L) }

        ListView(
            modifier.padding(it),
            musicList,
            currentMusic = playerState.currentMusic,
            isPlaying = playerState.isPlaying,
            progress = if(isSeeking) seekProgress else playerState.progress,
            duration = playerViewModel.getDuration(),
            onItemClick = {music->
                playerViewModel.playMusic(music)
            },
            onSeeking = {progress->
                isSeeking = true
                seekProgress = progress
            },
            onSeekFinished = {progress->
                isSeeking = false
                seekProgress = progress
                playerViewModel.seekMusic(progress)
            },
            onToggleClick = {isPlaying->
                playerViewModel.toggleMusic()
            },
            onPrevMusicClick = {
                val index = musicList.indexOf(playerState.currentMusic)
                val prevIndex = if(index==0) musicList.lastIndex else index-1
                playerViewModel.playMusic(musicList[prevIndex])
            },
            onNextMusicClick = {
                val index = musicList.indexOf(playerState.currentMusic)
                val nextIndex = if(index==musicList.lastIndex) 0 else index+1
                playerViewModel.playMusic(musicList[nextIndex])
            },
        )
        LoadingDialog(isLoading, "Scanning file ...")
    }
}

@Composable
fun ListView(modifier: Modifier = Modifier, musicList: List<MusicFile>,
             currentMusic: MusicFile = MusicFile(""),
             isPlaying: Boolean = false, progress: Long = 0L, duration: Long,
             onItemClick: (MusicFile)->Unit = {},
             onSeeking: (Long)->Unit = {},
             onSeekFinished: (Long)->Unit = {},
             onToggleClick: (Boolean) -> Unit = {},
             onPrevMusicClick: ()->Unit = {},
             onNextMusicClick: ()->Unit = {}) {
//    println("progress:$progress")
//    println("currentMusic:${currentMusic.filename}")

    Column {
        MusicList(
            modifier
                .fillMaxWidth()
                .weight(1f),
            musicList,
            currentMusic
        ) {
            onItemClick(it)
        }
        MusicControlBar(
            modifier = Modifier.fillMaxWidth(),
            isPlaying = isPlaying,
            currentMusic = currentMusic,
            position = progress,
            duration = duration,
            onValueChange = {
                onSeeking(it)
            },
            onValueChangeFinish = {
                onSeekFinished(progress)
            },
            onToggleClick = {
                onToggleClick(isPlaying)
            },
            onPrevMusicClick = onPrevMusicClick,
            onNextMusicClick = onNextMusicClick
        )
    }
}

@Composable
fun MusicControlBar(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    currentMusic: MusicFile?,
    position: Long,
    duration: Long,
    onValueChange: (Long)->Unit,
    onValueChangeFinish: ()->Unit,
    onToggleClick: ()->Unit = {},
    onPrevMusicClick: ()->Unit = {},
    onNextMusicClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp, 25.dp, 10.dp, 10.dp)
        ) {
            // music info
            Column (
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 5.dp)
            ) {
                Text(
                    text = currentMusic?.filename?:"",
                    fontSize = 16.sp
                )
                Text(
                    text = "<artist>",
                    fontSize = 13.sp
                )
            }

            // controller
            Row (
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // button prev
                Button(
                    modifier = Modifier
                        .width(50.dp)
                        .height(30.dp),
                    onClick = { onPrevMusicClick() }
                ) {
//                    Image(
//                        painter = painterResource(id = android.R.drawable.ic_media_previous),
//                        contentDescription = "prev_music"
//                    )
                    Text(text = "<", fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.width(5.dp))
                // button toggle
                Button(
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp),
                    onClick = { onToggleClick() }
                ) {
//                    Image(
//                        painter = painterResource(id = android.R.drawable.ic_media_play),
//                        contentDescription = "toggle_music"
//                    )
                    var text = "P"
                    if(isPlaying) {
                        text = "S"
                    }
                    Text(text = text, fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.width(5.dp))
                // button next
                Button(
                    modifier = Modifier
                        .width(50.dp)
                        .height(30.dp),
                    onClick = { onNextMusicClick() }
                ) {
//                    Image(
//                        painter = painterResource(id = android.R.drawable.ic_media_next),
//                        contentDescription = "next_music"
//                    )
                    Text(text = ">", fontSize = 10.sp)
                }
            }
        }
        // time
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp, 8.dp, 5.dp, 0.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Left,
                text = milliSecondsToTimeString(position),
                fontSize = 15.sp
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                text = milliSecondsToTimeString(duration),
                fontSize = 15.sp
            )
        }
        // progress bar
        ProgressBar(
            position = position,
            duration = duration,
            onValueChange = onValueChange,
            onValueChangeFinish = onValueChangeFinish,
            modifier = modifier
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun ProgressBar(
    position: Long,
    duration: Long,
    onValueChange: (Long)->Unit,
    onValueChangeFinish: ()->Unit,
    modifier: Modifier = Modifier
) {
    Slider(
        value = position.toFloat(),
        onValueChange = {
            onValueChange(it.toLong())
        },
        onValueChangeFinished = {
            onValueChangeFinish()
        },
        valueRange = 0f..duration.toFloat(),
        modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
    )
}

@Composable
fun MusicList(modifier: Modifier = Modifier, musicList: List<MusicFile>, currentMusic: MusicFile? = null, onClick: (MusicFile) -> Unit) {
    LazyColumn(
        modifier = modifier
    ){
        items(musicList) {
                music -> MusicItem(music=music, isPlaying = (music==currentMusic), onClick = onClick)
        }
    }
}

@Composable
fun MusicItem(modifier: Modifier = Modifier, music: MusicFile, isPlaying: Boolean, onClick: (MusicFile)->Unit = {}) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick(music)
                }
        ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(35.dp, 10.dp, 10.dp, 10.dp)
            ) {
                Column {
                    // filename
                    Text(text = music.filename)
                    // dir
                    Text(
                        text = music.dir,
                        fontSize = 10.sp
                    )
                }
                // music type
                Text(
                    text = music.type,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 2.dp, end = 5.dp)
                )
            }
            if(isPlaying) {
                RandomJumpingSpectrum(
                    Modifier
                        .padding(7.dp)
                        .size(20.dp))
            }
        }
        Divider()
    }
}

@Preview(showBackground = true)
@Composable
fun ListItemPreview() {
    MusicPlayerTheme {
//        MainScreen(Modifier.fillMaxSize(), musicList = emptyList(), isLoading = false)
//        MusicItem(MusicFile("file://folder/my_music/music_01.mp3"))
//        MusicList(Datasource().loadMusicFiles())
        val music = MusicFile("file://folder/my_music/music_01.mp3")
        MusicControlBar(Modifier.fillMaxWidth(),false, music,0,1200,{}, {})
//        CircularProgressIndicator()
    }
}

@Preview(showBackground = true)
@Composable
fun MusicItemPreview() {
    MusicItem(music = MusicFile("file://folder/my_music/music_01.mp3"), isPlaying = true)
}